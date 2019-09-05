package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.model.EntityType;
import com.simulationlab.QA_2.model.HostHolder;
import com.simulationlab.QA_2.service.CommentService;
import com.simulationlab.QA_2.service.QuestionService;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.parser.Entity;
import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(Model model,
                             @RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) return "redirect:/reglogin";
            int userId = hostHolder.getUser().getId();
            Comment comment = new Comment(userId, content, new Date(), questionId, EntityType.ENTITY_QUESTION, 0);
            System.out.println(String.format("comment: %s", comment.getContent()));
            commentService.addComment(comment);
            eventProducer.fireEvent(new EventModel().setActorId(userId)
            .setEntityType(EntityType.ENTITY_QUESTION)
            .setEntityId(questionId)
            .setEntityOwnerId(questionService.getById(questionId).getUserId())
            .setType(EventType.COMMENT));

            // some update, including question
            int cnt = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(questionId, cnt);

            // 事件触发

        } catch (Exception e) {
            logger.error("发表评论失败" + e.getMessage());
        }
        return "redirect:/question/" + String.valueOf(questionId);
    }
}
