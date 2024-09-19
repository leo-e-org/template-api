package com.it.template.api.repository.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.config.cache.custom-key-ttl:3600}")
    private Long keyTimeout;

    public String getValue(String key) {
        return StringUtils.defaultString(redisTemplate.opsForValue().get(key));
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value, keyTimeout, TimeUnit.SECONDS);
    }
}
