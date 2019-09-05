package com.simulationlab.QA_2.async;

import com.simulationlab.QA_2.model.EntityType;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}
