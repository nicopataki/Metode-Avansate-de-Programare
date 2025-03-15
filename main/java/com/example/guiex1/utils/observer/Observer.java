package com.example.guiex1.utils.observer;


import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.FriendshipEntityChangeEvent;

public interface Observer<E extends Event> {
    void update(E e);

}