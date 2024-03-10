package com.example.project_sa.observer;

import com.example.project_sa.event.Event;
import com.example.project_sa.event.FriendshipChangeEvent;
import com.example.project_sa.event.MessageChangeEvent;
import com.example.project_sa.event.UserChangeEvent;

public interface Observable <E extends Event>{
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObserver(E t);

    void notifyObservers(UserChangeEvent t);

    void notifyObservers(FriendshipChangeEvent t);

    void notifyObservers(MessageChangeEvent t);
}
