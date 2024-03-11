package com.wv.wvojcodesendbox.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

/**
 * 滑动窗口限流
 * @author wv
 * @version V1.0
 * @date 2024/3/3 15:53
 */
@Service
public class RedisLimiterManager {


//    @Value("${spring.redis.host}")
//    public static String host;
//    @Value("${spring.redis.port}")
//    public static Integer port;

    /**
     * Jedis 连接池
     */
//    public static final JedisPool JEDIS_POOLED = new JedisPool(host, port);
    public static final JedisPool JEDIS_POOLED = new JedisPool("127.0.0.1", 6379);


    public boolean isLimit(String key,int limit){
        try (Jedis jedis = JEDIS_POOLED.getResource()) {
            jedis.set("clientName", "Jedis");
            // 调用滑动窗口限流器
            SlidingWindowRateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiter(jedis, key, limit);
            return slidingWindowRateLimiter.allowRequest(key);
        }
    }
}
