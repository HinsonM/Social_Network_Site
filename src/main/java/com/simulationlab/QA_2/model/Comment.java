package com.simulationlab.QA_2.model;

import java.util.Date;

public class Comment {
    private int id;
    private int userId;
    private String content;
    private Date createdDate;
    private int entityId;
    private int entityType;
    private int status; // 1为删除

    public Comment() { } // 默认的构造函数，用于从DAO层去select返回后构造Comment，因为select是返回Comment的所有data member，因此下面的构造函数不适用

    public Comment(int userId, String content, Date createdDate, int entityId, int entityType, int status) {
        this.userId = userId;
        this.content = content;
        this.createdDate = createdDate;
        this.entityId = entityId;
        this.entityType = entityType;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
