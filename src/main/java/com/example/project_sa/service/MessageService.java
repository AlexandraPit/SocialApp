package com.example.project_sa.service;

import com.example.project_sa.domain.Message;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.ChangeEventType;
import com.example.project_sa.event.FriendshipChangeEvent;
import com.example.project_sa.event.MessageChangeEvent;
import com.example.project_sa.event.UserChangeEvent;
import com.example.project_sa.observer.Observable;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.repository.FriendshipRepository;
import com.example.project_sa.repository.FriendshipRequestRepository;
import com.example.project_sa.repository.MessageRepository;
import com.example.project_sa.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MessageService implements Observable<MessageChangeEvent> {
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private MessageRepository messageRepository;

    private FriendshipRequestRepository friedshipRequestRepository;
    private List<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    public MessageService(UserRepository userRepository, FriendshipRepository friendshipRepository,
                          FriendshipRequestRepository friedshipRequestRepository,
                          MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.friedshipRequestRepository = friedshipRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public List<Message> conversationBetween(User from, User to) {
        ArrayList<Message> messages = new ArrayList<>();
        this.messageRepository.findAll().forEach(messages::add);
        Predicate<Message> predicate = new Predicate<Message>() {
            @Override
            public boolean test(Message message) {
                if (message.getFrom().equals(from) && message.getTo().contains(to))
                    return true;
                else if (message.getFrom().equals(to) && message.getTo().contains(from))
                    return true;
                return false;

            }
        };
        List<Message> convo = messages.stream().filter(predicate).toList();
        return convo;
    }

    public void sendMessage(User from, User to, String message) {
        Message msg = new Message(from, message);
        ArrayList<User> toUser = new ArrayList<>();
        toUser.add(to);
        msg.setTo(toUser);
        this.messageRepository.save(msg);
        notifyObservers(new MessageChangeEvent(ChangeEventType.SEND, msg));
    }

    public void sendMessageToMultipleUsers(User from, ArrayList<User> to, String message) {
        Message msg = new Message(from, message);
        msg.setTo(to);
        this.messageRepository.save(msg);
        notifyObservers(new MessageChangeEvent(ChangeEventType.SEND, msg));
    }


    @Override
    public void addObserver(Observer<MessageChangeEvent> e) { this.observers.add(e);}

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {this.observers.remove(e);}

    @Override
    public void notifyObserver(MessageChangeEvent t) {observers.stream().forEach(x->x.update(t));

    }

    @Override
    public void notifyObservers(UserChangeEvent t) {

    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {

    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {

    }
}