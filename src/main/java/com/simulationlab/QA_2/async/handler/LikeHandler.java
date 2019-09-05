package com.simulationlab.QA_2.async.handler;

import com.simulationlab.QA_2.async.EventHandler;
import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.Message;
import com.simulationlab.QA_2.service.MessageService;
import com.simulationlab.QA_2.service.QuestionService;
import com.simulationlab.QA_2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component // component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Override
    public void doHandle(EventModel model) {
        String content = String.format("用户%s赞了你对问题%s的评论",
                userService.getUser(model.getActorId()).getName(), questionService.getById(Integer.parseInt(model.getExt("questionId"))));
        Message message = new Message(model.getActorId(), model.getEntityOwnerId(), content, new Date(), 0);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
