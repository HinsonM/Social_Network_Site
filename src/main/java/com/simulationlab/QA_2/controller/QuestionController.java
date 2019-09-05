package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.*;
import com.simulationlab.QA_2.service.*;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;
import java.beans.EventSetDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path={"/question/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(Model model,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() == null) {
                question.setUserId(QaUtil.ANONYMOUS_ID);
                // 该处的ANONYMOUS_ID必须是真实的userId，否则从数据中取不出来，在html response[i].objs.user.headUrl为null,则无法执行
                // 但是不奏效，因此暂时加上跳转的页面

            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                eventProducer.fireEvent(new EventModel().setType(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId()).setEntityId(question.getId())
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));

                return QaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("问题添加失败" + e.getMessage());
        }
        return QaUtil.getJSONString(1, "失败");
    }

    // 一般而言，POST方法才会有try, catch来处理异常
    @RequestMapping(path={"/question/{qid}"}, method = {RequestMethod.GET})
    public String questionDetail(Model model,
                                 @PathVariable("qid") int qid) {
        Question question = questionService.getById(qid);
        model.addAttribute("question", question);

        // 问题的粉丝详情
        List<Integer> userIds = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 0, 10);
        List<User> users = new ArrayList<User>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            users.add(user);
        }
        model.addAttribute("users", users);
        model.addAttribute("size", users.size());
        boolean isFollower = false;
        if(hostHolder.getUser() != null) {
            isFollower = followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid);
        }
        model.addAttribute("follow", isFollower);

        // 问题的评论详情
        List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("vos", vos);
        return "detail";
    }
}
