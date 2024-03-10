package com.example.project_sa.domain;

public class FriendshipRequest extends Entity<Tuple<Long, Long>>{

    private User from;
    private User to;
    private String status;

    public FriendshipRequest() {
        // Default constructor without arguments
    }
    public FriendshipRequest(User from, User to){
        this.from=from;
        this.to=to;
    }
    public void setStatus(String status){this.status=status;}
    public String getStatus(){return status;}
    public User getFrom(){return from;}
    public User getTo() {return to;}
    @Override
    public String toString() {
        return "Friend Request from "+this.from.getUsername()+ " to "+this.to.getUsername()+".";
    }
}
