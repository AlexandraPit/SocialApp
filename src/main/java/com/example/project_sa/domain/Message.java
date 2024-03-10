package com.example.project_sa.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message extends Entity<Long>{
    protected User from;
    protected ArrayList<User> to = new ArrayList<>();

    protected String message;

    protected LocalDateTime date;

    public Message(User from, String message){
        this.from = from;
        this.message = message;
    }

    public User getFrom() {
        return from;
    }

    public ArrayList<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(ArrayList<User> to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from = " + from.getFirst_name() +" " +from.getLast_name()+ "\n"+
                ", to = " + to + "\n"+
                ", message = '" + message + "'\n" +
                ", date = " + date +
                '}';
    }
}
