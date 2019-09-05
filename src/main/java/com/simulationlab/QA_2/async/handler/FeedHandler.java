package com.simulationlab.QA_2.async.handler;


import com.alibaba.fastjson.JSONObject;
import com.simulationlab.QA_2.async.EventHandler;
import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.EntityType;
import com.simulationlab.QA_2.model.Feed;
import com.simulationlab.QA_2.model.Question;
import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.service.*;
import com.simulationlab.QA_2.util.JedisAdapter;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Component;

import javax.lang.model.element.ElementVisitor;
import java.lang.reflect.Array;
import java.util.*;

@Component // 为什么要加Component
public class FeedHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FollowService followService;

    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<String, String>();
        User user = userService.getUser(model.getActorId());
        if (user == null) return null;
        map.put("userName", user.getName());
        map.put("userHead", user.getHeadUrl());
        map.put("userId", String.valueOf(user.getId()));

        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(model.getEntityId());
            if (question == null) return null;
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            map.put("questionContent", question.getContent());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        // 永久化存储feed到mysql中，完成这一步，pull即可进行

        // 当A取关B时（A，B均为用户），从A的timeline中删除B的feed
        if (model.getType() == EventType.UNFOLLOW && model.getEntityType() == EntityType.ENTITY_USER) {
            String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId());
            List<Feed> feeds = feedService.selectUserFeeds(Integer.MAX_VALUE, Arrays.asList(model.getEntityId()), Integer.MAX_VALUE);
            // 删除缓存中用户相关的Feed的id（一个队列），但是在mysql中的feed是永久化的
            for (Feed f : feeds) {
                jedisAdapter.lrem(timelineKey, 0, String.valueOf(f.getId()));
            }
        } else if (model.getType() == EventType.FOLLOW || model.getType() == EventType.COMMENT) {
            // 构造feed
            Feed feed = new Feed(model.getType().getValue(), model.getActorId(), new Date(), buildFeedData(model));
            if (feed.getData() == null) return; //  不支持的feed
            feedService.addFeed(feed); // feed永久存储，Pull可进行

            // 推送给活跃的粉丝
            List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
            for (Integer followerId : followerIds) {
                // 该粉丝为活跃用户，推给他
                User follower = userService.getUser(followerId);
                long diff = new Date().getTime() - follower.getRecentLoginTime().getTime();
                if ( diff < 24 * 60 * 60 * 1000) {
                    String timelineKey = RedisKeyUtil.getTimelineKey(followerId);
                    jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
                }
            }
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW, EventType.COMMENT, EventType.UNFOLLOW});
    }
}
