package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.dao.FeedDAO;
import com.simulationlab.QA_2.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Autowired
    FeedDAO feedDAO;

    public int addFeed(Feed feed) {
        return feedDAO.addFeed(feed) > 0 ? feed.getUserId(): 0;
    }

    public Feed selectById(int id) {
        return feedDAO.selectById(id);
    }

    public List<Feed> selectUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }
}
