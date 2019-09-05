package com.simulationlab.QA_2.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simulationlab.QA_2.service.RedisKeyUtil;
import com.simulationlab.QA_2.util.JedisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        String key = RedisKeyUtil.getEventQueueKey();
        Jedis jedis = jedisAdapter.getJedis();
        jedis.del(key);
        // 初始化config，感觉每次起来应该清一清redis的queue
        // ???下面要做什么我直到，机制是什么呢？一开始当consumer被设置时，便执行下面函数，收集handler类族的信息，创建config，接着时启动线程轮询处理事件
        // 意思就是当本对象的all their properties have been set，就要用该方法进行初始化
        // Interface to be implemented by beans that need to react once all their properties have been set by a BeanFactory:
        // e.g. to perform custom initialization, or merely to check that all mandatory properties have been set.
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType eventType : eventTypes) {
                    if (!config.containsKey(eventType)) {
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }


        // 启动线程处理各种event
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        String key = RedisKeyUtil.getEventQueueKey();
//                        if (key == null) continue;
                        // 由于jedis.brpop弹出格式如下，因此取出来要做一些额外操作
                        // 127.0.0.1:6379> brpop mmlist 0
                        // 1) "mmlist"
                        // 2) "1"
                        List<String> events = jedisAdapter.brpop(0, key);

                        for (String jevent : events) {
                            if (jevent.equals(key)) continue;

                            EventModel eventModel = JSON.parseObject(jevent, EventModel.class);

                            if (!config.containsKey(eventModel.getType())) {
                                logger.error("无法识别事件 : "+ eventModel.getType());
                                continue;
                            }

                            for (EventHandler handler : config.get(eventModel.getType())) {
                                handler.doHandle(eventModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理事件失败" + e.getMessage());
                }
            }
        });
        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
