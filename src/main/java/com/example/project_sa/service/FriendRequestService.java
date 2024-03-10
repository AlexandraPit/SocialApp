package com.example.project_sa.service;

import com.example.project_sa.UserScreen;
import com.example.project_sa.domain.Friendship;
import com.example.project_sa.domain.FriendshipRequest;
import com.example.project_sa.domain.Tuple;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.ChangeEventType;
import com.example.project_sa.event.FriendshipChangeEvent;
import com.example.project_sa.event.MessageChangeEvent;
import com.example.project_sa.event.UserChangeEvent;
import com.example.project_sa.observer.Observable;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.repository.FriendshipRepository;
import com.example.project_sa.repository.FriendshipRequestRepository;
import com.example.project_sa.repository.UserRepository;

import java.util.*;

public class FriendRequestService implements Observable<FriendshipChangeEvent> {
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;


    private FriendshipRequestRepository friedshipRequestRepository;
    private List<com.example.project_sa.observer.Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    public FriendRequestService(UserRepository userRepository,
                                FriendshipRepository friendshipRepository,
                                FriendshipRequestRepository friedshipRequestRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friedshipRequestRepository = friedshipRequestRepository;
    }

    public ArrayList<FriendshipRequest> pendingFriendshipRequests(Long id) {
        ArrayList<FriendshipRequest> pendingFriendshipRequests = new ArrayList<>();
        this.friedshipRequestRepository.findAll().forEach(f -> {
            if (f.getTo().getId() == id && f.getStatus().equals("pending"))
                pendingFriendshipRequests.add(f);
        });
        return pendingFriendshipRequests;
    }


    public void acceptFriendRequest(Tuple<Long, Long> id) {
        FriendshipRequest fr = this.friedshipRequestRepository.findOne(id).get();
        fr.setStatus("accepted");
        this.friedshipRequestRepository.update(fr);

        User from_user = fr.getFrom();
        User to_user = fr.getTo();

        ArrayList<User> friendsUser1 = from_user.getFriend_list();
        friendsUser1.add(to_user);
        from_user.setFriend_list(friendsUser1);

        ArrayList<User> friendsUser2 = to_user.getFriend_list();
        friendsUser2.add(from_user);
        to_user.setFriend_list(friendsUser2);

        this.friendshipRepository.save(new Friendship(from_user.getId(), to_user.getId()));
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.ACCEPT, fr));

    }

    public void declineFriendRequest(Tuple<Long, Long> id) {
        FriendshipRequest fr = this.friedshipRequestRepository.findOne(id).get();
        fr.setStatus("declined");
        this.friedshipRequestRepository.update(fr);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.DECLINE, fr));
    }

    public void deleteFriend(Long from, Long to) {
        //User toBeDeletedFrom = this.userRepository.findOne(from).get();
        //User toBeDeleted = this.userRepository.findOne(to).get();

        Optional<Friendship> f1 = this.friendshipRepository.findOne(new Tuple<>(from, to));
        Optional<Friendship> f2 = this.friendshipRepository.findOne(new Tuple<>(to, from));

        if (f1.isPresent()) {
            this.friendshipRepository.delete(f1.get().getId());
        } else {
            this.friendshipRepository.delete(f2.get().getId());
        }

//        ArrayList<User> fl1 = toBeDeletedFrom.getFriend_list();
//        fl1.remove(toBeDeleted);
//        toBeDeletedFrom.setFriend_list(fl1);

//        ArrayList<User> fl2 = toBeDeleted.getFriend_list();
//        fl2.remove(toBeDeletedFrom);
//        toBeDeleted.setFriend_list(fl2);

        notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, null));

    }

    public void sendFriendRequest(Long from, Long to) {
        User fromUser = this.userRepository.findOne(from).get();
        User toUser = this.userRepository.findOne(to).get();
        Optional<FriendshipRequest> optionalFriendshipRequest = this.friedshipRequestRepository.findOne(new Tuple<>(from, to));
        if (optionalFriendshipRequest.isEmpty()) {
            FriendshipRequest fr = new FriendshipRequest(fromUser, toUser);
            fr.setId(new Tuple<>(from, to));
            this.friedshipRequestRepository.save(fr);
        } else if (optionalFriendshipRequest.get().getStatus().equals("declined")) {
            FriendshipRequest fr = optionalFriendshipRequest.get();
            fr.setStatus("pending");
        }
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, null));
    }

    public Set<User> possibleFriends(Long id) {
        Set<User> possibleFriends = new HashSet<>();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Friendship> friendships = new ArrayList<>();
        ArrayList<FriendshipRequest> friendshipRequests = new ArrayList<>();
        this.friedshipRequestRepository.findAll().forEach(friendshipRequests::add);
        this.userRepository.findAll().forEach(users::add);
        this.friendshipRepository.findAll().forEach(friendships::add);
        Boolean friendshipFree;
        Boolean frienshipRequestFree;
        for (User u : users) {
            if (u.getId() != id) {
                friendshipFree = true;
                frienshipRequestFree = true;
                for (Friendship f : friendships) {
                    if ((f.getId().getLeft() == u.getId() && f.getId().getRight() == id) || (f.getId().getRight() == u.getId() && f.getId().getLeft() == id)) {
                        friendshipFree = false;
                    }
                }
                for (FriendshipRequest fr : friendshipRequests) {
                    if (fr.getTo().getId() == u.getId() && fr.getFrom().getId() == id && fr.getStatus().equals("pending"))
                        frienshipRequestFree = false;
                    if (fr.getTo().getId() == u.getId() && fr.getFrom().getId() == id && fr.getStatus().equals("accepted"))
                        frienshipRequestFree = false;
                }

                if (friendshipFree && frienshipRequestFree)
                    possibleFriends.add(u);
            }

        }
        return possibleFriends;
    }

    public void addObserver(UserScreen e) {
        observers.add(e);
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {

    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObserver(FriendshipChangeEvent t) {

    }

    @Override
    public void notifyObservers(UserChangeEvent t) {

    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {

    }
}