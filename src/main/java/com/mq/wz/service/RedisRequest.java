package com.mq.wz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

/**
 * @author wazh
 * @since 2023-11-29-20:06
 */
public class RedisRequest {
    public static void addCache(Object obj) {
        try (
                Jedis jedis = new Jedis("8.130.8.46", 6379)
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(obj);
            jedis.set("category-" + obj.getClass().getDeclaredField("name").getName(), value);
        } catch (JsonProcessingException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
