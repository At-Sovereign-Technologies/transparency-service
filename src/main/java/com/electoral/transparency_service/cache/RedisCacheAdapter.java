package com.electoral.transparency_service.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisCacheAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public void set(String key, Object value) {
        try {
            String json = mapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing cache", e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (data == null) return null;

            return mapper.readValue(data.toString(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing cache", e);
        }
    }
}