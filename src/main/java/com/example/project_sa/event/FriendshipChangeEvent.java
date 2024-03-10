package com.example.project_sa.event;

import com.example.project_sa.domain.FriendshipRequest;

public class FriendshipChangeEvent implements Event{
    private ChangeEventType type;
    private FriendshipRequest data, oldData;

    public FriendshipChangeEvent(ChangeEventType type, FriendshipRequest data) {
        this.type = type;
        this.data = data;
    }
    public FriendshipChangeEvent(ChangeEventType type, FriendshipRequest data, FriendshipRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendshipRequest getData() {
        return data;
    }

    public FriendshipRequest getOldData() {
        return oldData;
    }
}
