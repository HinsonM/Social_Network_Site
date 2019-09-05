package com.simulationlab.QA_2.async;

import com.alibaba.fastjson.JSONObject;
import com.simulationlab.QA_2.service.RedisKeyUtil;
import com.simulationlab.QA_2.service.SensitiveService;
import com.simulationlab.QA_2.util.JedisAdapter;
import com.simulationlab.QA_2.util.QaUtil;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class EventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            logger.error("事件发送失败" + e.getMessage());
            return false;
        }

    }
}
