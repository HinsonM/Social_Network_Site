package com.simulationlab.QA_2.model;

import java.util.Date;

public class Message {
    private int id;
    private int fromid;
    private int toid;
    private String conversationId;
    private String content;
    private Date createdDate;
    private int hasRead; // 0未读， 1已读

    public Message() {}

    public Message(int fromid, int toid, String content, Date createdDate, int hasRead) {
        this.fromid = fromid;
        this.toid = toid;
        if (fromid < toid) this.conversationId = String.format("%d_%d", fromid, toid);
        else this.conversationId = String.format("%d_%d", toid, fromid);
        this.conversationId = conversationId;
        this.content = content;
        this.createdDate = createdDate;
        this.hasRead = hasRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromid() {
        return fromid;
    }

    public void setFromid(int fromid) {
        this.fromid = fromid;
        if (this.fromid < this.toid) this.conversationId = String.format("%d_%d", this.fromid, this.toid);
        else this.conversationId = String.format("%d_%d", this.toid, this.fromid);
    }

    public int getToid() {
        return toid;
    }

    public void setToid(int toid) {
        this.toid = toid;
        if (this.fromid < this.toid) this.conversationId = String.format("%d_%d", this.fromid, this.toid);
        else this.conversationId = String.format("%d_%d", this.toid, this.fromid);
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getHasRead() {
        return hasRead;
    }

    public void setHasRead(int hasRead) {
        this.hasRead = hasRead;
    }
}
