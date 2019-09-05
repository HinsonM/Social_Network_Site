package com.simulationlab.QA_2.util;

import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.model.EntityType;
import com.simulationlab.QA_2.model.Question;
import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.service.CommentService;
import com.simulationlab.QA_2.service.LikeService;
import com.simulationlab.QA_2.service.QuestionService;
import com.simulationlab.QA_2.service.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScoringSystem {
    private static final Logger logger = LoggerFactory.getLogger(ScoringSystem.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    LikeService likeService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    CommentService commentService;

    /**
     * 问题的价值评估
     * @param question
     * @return
     */
    public double scoreQuestion(Question question) {
        // 浏览数
        int Qviews = question.getViews(); // 预留接口
        // 回答数
        int Qanswer = question.getCommentCount();
        //赞踩差
        String likeKey = RedisKeyUtil.getLikeKey(EntityType.ENTITY_QUESTION, question.getId());
        String dislikeKey = RedisKeyUtil.getDisLikeKey(EntityType.ENTITY_QUESTION, question.getId());
        long Qscore = jedisAdapter.scard(likeKey) - jedisAdapter.scard(dislikeKey);
        // 回答赞踩差
        long sum = 0;
        List<Comment> comments = new ArrayList<Comment>();
//        comments = getQuestionComments(questionId);
        for (Comment comment : comments) {
            String commentLikeKey = RedisKeyUtil.getLikeKey(EntityType.ENTITY_COMMENT, comment.getId());
            String commentDislikeKey = RedisKeyUtil.getDisLikeKey(EntityType.ENTITY_COMMENT, comment.getId());
            sum += jedisAdapter.scard(commentLikeKey) - jedisAdapter.scard(commentDislikeKey);
        }


        // 题目发布时间差
        long QageInHours = new Date().getTime() - question.getCreatedDate().getTime();

        // 从sql语句中读取最新回答时间
        long Qupdated = question.getCreatedDate().getTime() + 3600*24;
        double numerator = Math.log(Qviews*4) + ((Qanswer*Qscore)/5) + sum;
        double denominator = Math.pow(((QageInHours+1) - ((QageInHours - Qupdated)/2)),1.5);
        return numerator/denominator;

    }

    /**
     * 用户对社区的贡献值：留一个接口
     * @param user
     * @return
     */
    public static double scoreUser(User user) {
        int ansCount = 0;
        int loginTimes = 0;
        double numerator = 0;
        double denominator = 0;
        return numerator/denominator;
    }

}
