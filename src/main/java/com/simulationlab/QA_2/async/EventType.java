package com.simulationlab.QA_2.async;

public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    ADD_QUESTION(6);

    EventType(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }
}
