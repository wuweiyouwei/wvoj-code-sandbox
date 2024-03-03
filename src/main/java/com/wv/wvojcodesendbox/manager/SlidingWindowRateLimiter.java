package com.wv.wvojcodesendbox.manager;

import redis.clients.jedis.Jedis;

public class SlidingWindowRateLimiter {
    private Jedis jedis;
    private String key;
    private int limit;

    public SlidingWindowRateLimiter(Jedis jedis, String key, int limit) {
        this.jedis = jedis;
        this.key = key;
        this.limit = limit;
    }

//    /**
//     *  判断请求是否被允许（有并发安全）
//     * @param key
//     * @return
//     */
//    public boolean allowRequest(String key) {
//        //当前时间戳
//        long currentTime = System.currentTimeMillis();
//        //窗口开始时间是当前时间减60s
//        long windowStart = currentTime - 60 * 1000;
//        //删除窗口开始时间之前的所有数据
//        jedis.zremrangeByScore(key, "-inf", String.valueOf(windowStart));
//        //计算总请求数
//        long currentRequests = jedis.zcard(key);
//    	//窗口足够则把当前请求加入
//        if (currentRequests < limit) {
//            jedis.zadd(key, currentTime, String.valueOf(currentTime));
//            return true;
//        }
//        return false;
//    }

    public boolean allowRequest(String key) {
        // 当前时间戳
        long currentTime = System.currentTimeMillis();
        // 使用Lua脚本来确保原子性操作
        String luaScript = "local window_start = ARGV[1] - 1000\n" +
                "redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', window_start)\n" +
                "local current_requests = redis.call('ZCARD', KEYS[1])\n" +
                "if current_requests < tonumber(ARGV[2]) then\n" +
                "    redis.call('ZADD', KEYS[1], ARGV[1], ARGV[1])\n" +
                "    return 1\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        Object result = jedis.eval(luaScript, 1, key, String.valueOf(currentTime), String.valueOf(limit));

        return (Long) result == 1;
    }
}