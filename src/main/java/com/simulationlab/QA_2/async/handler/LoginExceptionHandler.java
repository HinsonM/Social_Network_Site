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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Override
    public void doHandle(EventModel model) {

        // xxxx判断发现这个用户登陆异常
        String content = String.format("用户%s于%s在某地登录",
                userService.getUser(model.getEntityOwnerId()).getName(), model.getExt("date"));
        Message message = new Message(model.getActorId(), model.getEntityOwnerId(), content, new Date(), 0);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
