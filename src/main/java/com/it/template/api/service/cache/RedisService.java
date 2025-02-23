package com.it.template.api.service.cache;

import com.it.template.api.repository.cache.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisRepository redisRepository;

    public Mono<String> getValue(String key) {
        return redisRepository.getValue(key);
    }

    public Mono<Boolean> setValue(String key, String value) {
        return redisRepository.setValue(key, value);
    }
}
