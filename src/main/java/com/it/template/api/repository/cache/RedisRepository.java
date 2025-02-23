package com.it.template.api.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${app.config.cache.custom-key-ttl:3600}")
    private Long keyTimeout;

    public Mono<String> getValue(String key) {
        return reactiveRedisTemplate.opsForValue().get(key).defaultIfEmpty("");
    }

    public Mono<Boolean> setValue(String key, String value) {
        return reactiveRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(keyTimeout));
    }
}
