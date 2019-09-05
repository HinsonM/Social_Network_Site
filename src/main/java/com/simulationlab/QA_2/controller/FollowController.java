package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventHandler;
import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.*;
import com.simulationlab.QA_2.service.CommentService;
import com.simulationlab.QA_2.service.FollowService;
import com.simulationlab.QA_2.service.QuestionService;
import com.simulationlab.QA_2.service.UserService;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followUser(Model model,
                             @RequestParam("userId") int userId) {
        User localUser =  hostHolder.getUser();
        if (localUser == null) {
            return "redirect:/reglogin";
        }
        try {
            boolean ret = followService.follow(localUser.getId(), EntityType.ENTITY_USER, userId);

            // 发送事件
            eventProducer.fireEvent(new EventModel().setType(EventType.FOLLOW).
                    setActorId(localUser.getId()).
                    setEntityType(EntityType.ENTITY_USER).
                    setEntityId(userId).
                    setEntityOwnerId(userId));
            // 返回关注的人数
            return QaUtil.getJSONString(ret ? 0 : 1,
                    String.valueOf(followService.getFolloweeCount(localUser.getId(), EntityType.ENTITY_USER)));
        } catch (Exception e) {
            logger.error("关注用户失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }

    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(Model model,
                               @RequestParam("userId") int userId) {
        User localUser =  hostHolder.getUser();
        if (localUser == null) {
            return "redirect:/reglogin";
        }
        try {
            boolean ret = followService.unfollow(localUser.getId(), EntityType.ENTITY_USER, userId);

            // 发送事件
            eventProducer.fireEvent(new EventModel().setType(EventType.UNFOLLOW).
                    setActorId(localUser.getId()).
                    setEntityType(EntityType.ENTITY_USER).
                    setEntityId(userId).
                    setEntityOwnerId(userId));
            // 返回关注的人数
            return QaUtil.getJSONString(ret ? 0 : 1,
                    String.valueOf(followService.getFolloweeCount(localUser.getId(), EntityType.ENTITY_USER)));
        } catch (Exception e) {
            logger.error("取关用户失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }


    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(Model model,
                             @RequestParam("questionId") int questionId) {
        User localUser =  hostHolder.getUser();
        if (localUser == null) {
            return "redirect:/reglogin";
        }
        try {

            Question q = questionService.getById(questionId);
            if (q == null) {
                return QaUtil.getJSONString(1, "问题不存在");
            }

            boolean ret = followService.follow(localUser.getId(), EntityType.ENTITY_QUESTION, questionId);

            // 发送事件
            eventProducer.fireEvent(new EventModel().setType(EventType.FOLLOW).
                    setActorId(localUser.getId()).
                    setEntityType(EntityType.ENTITY_QUESTION).
                    setEntityId(questionId).
                    setEntityOwnerId(q.getUserId()));

            Map<String, Object> info = new HashMap<>();
            info.put("headURL", localUser.getHeadUrl());
            info.put("name", localUser.getName());
            info.put("id", localUser.getId());
            // 返回关注的人数 问题不会关注别人，因此是问题的粉丝
            info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
            return QaUtil.getJSONString(ret ? 0 : 1, info);

        } catch (Exception e) {
            logger.error("关注问题失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }

    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(Model model,
                                 @RequestParam("questionId") int questionId) {
        User localUser =  hostHolder.getUser();
        if (localUser == null) {
            return "redirect:/reglogin";
        }
        try {

            Question q = questionService.getById(questionId);
            if (q == null) {
                return QaUtil.getJSONString(1, "问题不存在");
            }

            boolean ret = followService.unfollow(localUser.getId(), EntityType.ENTITY_QUESTION, questionId);

            // 发送事件
            eventProducer.fireEvent(new EventModel().setType(EventType.UNFOLLOW).
                    setActorId(localUser.getId()).
                    setEntityType(EntityType.ENTITY_QUESTION).
                    setEntityId(questionId).
                    setEntityOwnerId(q.getUserId()));

            Map<String, Object> info = new HashMap<>();
            info.put("id", hostHolder.getUser().getId());
            info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
            return QaUtil.getJSONString(ret ? 0 : 1, info);

        } catch (Exception e) {
            logger.error("取关问题失败" + e.getMessage());
        }
        return QaUtil.getJSONString(999);
    }

    /* 该用户被哪些用户所关注 */
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    /* 该用户关注了哪些用户 */
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
