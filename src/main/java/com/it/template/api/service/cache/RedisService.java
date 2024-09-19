package com.it.template.api.service.cache;

import com.it.template.api.repository.cache.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisRepository redisRepository;

    public String getValue(String key) {
        return redisRepository.getValue(key);
    }

    public void setValue(String key, String value) {
        redisRepository.setValue(key, value);
    }
}
