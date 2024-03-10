package com.example.project_sa.domain;

public class ReplyMessage extends Message {
    Long replyToID;

    public ReplyMessage(User from, String msg, Long replyToID) {
        super(from, msg);
        this.replyToID = replyToID;
    }

    public Long getReplyToID() {
        return replyToID;
    }

    public void setReplyToID(Long replyToID) {
        this.replyToID = replyToID;
    }

    @Override
    public String toString() {
        return "ReplyMessage{" +
                "reply to=" + replyToID +
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}