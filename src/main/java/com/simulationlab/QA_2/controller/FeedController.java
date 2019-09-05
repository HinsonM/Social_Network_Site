package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.model.*;
import com.simulationlab.QA_2.service.FeedService;
import com.simulationlab.QA_2.service.FollowService;
import com.simulationlab.QA_2.service.RedisKeyUtil;
import com.simulationlab.QA_2.service.UserService;
import com.simulationlab.QA_2.util.JedisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    private List<Feed> getPushFeeds(int localUserId) {
        String pushKey = RedisKeyUtil.getTimelineKey(localUserId);
        List<String> feedIds = jedisAdapter.lrange(pushKey, 0, 10);
        List<Feed> feeds = new ArrayList<Feed>();
        for (String feedId : feedIds) {
            Feed feed = feedService.selectById(Integer.parseInt(feedId));
            if (feed != null) feeds.add(feed);
        }
        return feeds;
    }


    private List<Feed> getPullFeeds(int localUserId) {
        List<Integer> followees = new ArrayList<Integer>();
        if(localUserId != 0) {
            followees = followService.getFollowees(EntityType.ENTITY_USER, localUserId, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.selectUserFeeds(Integer.MAX_VALUE, followees, 10);
        return feeds;
    }

    @RequestMapping(path = {"/feeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getFeeds(Model model) {
        try {
            User localUser = hostHolder.getUser();
            int localUserId = localUser != null ? localUser.getId() : 0;
            List<Feed> feeds = null;
            // 用户活跃度判断
            long activeDegree = new Date().getTime() - userService.getUser(localUserId).getRecentLoginTime().getTime();
            feeds = activeDegree < (24 * 60 * 60 * 1000) ? getPushFeeds(localUserId) : getPullFeeds(localUserId);
            model.addAttribute("feeds", feeds);
        } catch(Exception e) {
            logger.error("获取feed流失败" + e.getMessage());
        }
        return "feeds";
    }

}
