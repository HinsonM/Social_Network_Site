package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.model.EntityType;
import com.simulationlab.QA_2.model.HostHolder;
import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.service.CommentService;
import com.simulationlab.QA_2.service.LikeService;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(Model model,
                       @RequestParam("commentId") int commentId) { // 暂时参数设置为commentId，以后会相当于entityId
        try {
            User localUser = hostHolder.getUser();
            if(localUser == null) {
                return "redirect:/reglogin";
            }
            long likeCount = likeService.like(localUser.getId(), EntityType.ENTITY_COMMENT, commentId);

            // 赞了之后，发送异步事件
            Comment comment = commentService.getCommentById(commentId);
            eventProducer.fireEvent(new EventModel()
                    .setType(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityOwnerId(comment.getUserId())
                    .setExt("questionId", String.valueOf(comment.getEntityId())));

            return QaUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("评论失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(Model model,
                       @RequestParam("commentId") int commentId) { // 暂时参数设置为commentId，以后会相当于entityId
        try {
            User localUser = hostHolder.getUser();
            if(localUser == null) {
                return "redirect:/reglogin";
            }
            long likeCount = likeService.dislike(localUser.getId(), EntityType.ENTITY_COMMENT, commentId);
            return QaUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("评论失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }
}
