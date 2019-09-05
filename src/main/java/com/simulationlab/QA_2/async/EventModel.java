package com.simulationlab.QA_2.async;

import java.util.HashMap;
import java.util.Map;

/**
 * “事件”模型，保留事件发生现场的各种信息（按需），就好像收集案发现场的证据一样；
 *
 * 同时像一个xx模型：
 *      事件类型(type), 施加者(actorId: user)，施加对象实体(entity: (entityId, entityType) e.g. comment)，
 *      施加对象实体的拥有者(entityOwnerId: user), 扩展信息(extends: Map<String, String>, 如案发现场额外信息)
 */
public class EventModel {
    private EventType type;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String, String> exts = new HashMap<String, String>();

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String val) {
        exts.put(key, val);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
