package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.util.JedisAdapter;
import org.aspectj.apache.bcel.classfile.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

@Service
public class LikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    UserService userService;


    // like
    public long like(int userId, int entityId, int entityType) {
        // 假如like中
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        // 从dislike中删掉
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(dislikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey); // 返回Like的总数
    }

    // dislike
    public long dislike(int userId, int entityId, int entityType) {
        // 假如like中
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));

        // 从dislike中删掉
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey); // 返回Like的总数
    }

    // get count of like
    public long getLikeCount(int entityId, int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    // get count of dislike
    public long getDislikeCount(int entityId, int entityType) {
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.scard(dislikeKey);
    }

    public int getLikeStatus(int userId, int entityId, int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId)) == true)
            return 1;
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(dislikeKey, String.valueOf(userId)))
            return -1;
        return 0;
    }

}
