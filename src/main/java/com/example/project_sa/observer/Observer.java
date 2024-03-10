package com.example.project_sa.observer;

import com.example.project_sa.event.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
