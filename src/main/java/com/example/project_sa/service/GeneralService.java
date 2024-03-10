package com.example.project_sa.service;

import com.example.project_sa.domain.Events;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.*;
import com.example.project_sa.observer.Observable;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.repository.*;

import java.util.*;

public class GeneralService implements Observable<UserChangeEvent> {

    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private MessageRepository messageRepository;

    private FriendshipRequestRepository friedshipRequestRepository;
    private EventRepository eventRepository;
    public GeneralService(UserRepository userRepository,
                          FriendshipRepository friendshipRepository,
                          MessageRepository messageRepository,
                          FriendshipRequestRepository friedshipRequestRepository,
                          EventRepository eventRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.friedshipRequestRepository = friedshipRequestRepository;
        this.eventRepository=eventRepository;
    }

    public void addUser(User user){
        this.userRepository.save(user);
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD,user));
    }
public void addEvent(Events events){
        this.eventRepository.save(events);
      //notifyObservers(new EventChangeType(ChangeEventType.ADD,events));
}
    public void deleteUser(Long id){
        Optional<User> user = this.userRepository.findOne(id);
        if(user.isPresent())
            this.userRepository.delete(id);
        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE,user.get()));
    }

    public void deleteEvent(Long id){
        Optional<Events> event = this.eventRepository.findOne(id);
        if(event.isPresent())
            this.eventRepository.delete(id);
    }

    public void modifyUser(User user){
        User old = this.userRepository.findOne(user.getId()).get();
        this.userRepository.update(user);
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE,user,old));
    }

    public Iterable<User> findAllUsers(){
        return this.userRepository.findAll();
    }
    public Iterable<Events> findAllEvents(){
        return this.eventRepository.findAll();
    }

    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();

    public Optional<User> checkCredentials(String user_email, String user_password){
        Set<User> users = new HashSet<>();
        this.userRepository.findAll().forEach(users::add);
        for(User u:users){
            if(u.getEmail().equals(user_email) && u.getPassword().equals(user_password))
                return Optional.of(u);
        }
        return Optional.empty();
    }

    public ArrayList<User> friendsListForUser(Long id){
        if(this.userRepository.findOne(id).isPresent())
            return this.userRepository.findOne(id).get().getFriend_list();
        else
            return null;
    }

    public User findOne(Long ID){
        return this.userRepository.findOne(ID).get();
    }
    public Events finOne(Long ID){return this.eventRepository.findOne(ID).get();}




    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObserver(UserChangeEvent t) {observers.stream().forEach(x->x.update(t));

    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {

    }
   /* @Override
    public void notifyObservers(EventChangeEvent t) {

    }*/

    @Override
    public void notifyObservers(MessageChangeEvent t) {

    }
}
