package com.it.template.api.service;

import com.it.template.api.configuration.LettuceTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LettuceTestConfiguration.class})
// @Disabled("Redis Service Test disabled due to GitHub Cloud Runner unable to access Redis")
public class RedisServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTestTemplate;

    private static final Long DEFAULT_TIMEOUT = 5L;

    @Test
    void shouldAddKey() {
        redisTestTemplate.opsForValue().set("API_TEST_KEY", "testValue", DEFAULT_TIMEOUT, TimeUnit.MINUTES);
        Assertions.assertNotNull(redisTestTemplate.getExpire("API_TEST_KEY"));
    }

    @Test
    void shouldGetValueFromKey() {
        String value = redisTestTemplate.opsForValue().get("API_TEST_KEY");
        Assertions.assertFalse(StringUtils.isBlank(value));
        Assertions.assertEquals("testValue", value);
    }
}
