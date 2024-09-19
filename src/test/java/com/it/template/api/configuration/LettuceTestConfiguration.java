package com.it.template.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

@TestConfiguration
public class LettuceTestConfiguration {

    private static final String HOST = "localhost";
    private static final Integer PORT = 6379;
    private static final String PASSWORD = "pwd";
    private static final Integer CONNECTION_TIMEOUT = 5000;

    @Bean
    RedisTemplate<String, String> redisTestTemplate() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(createConnectionFactory());
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    private RedisConnectionFactory createConnectionFactory() {
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration
                .builder()
                .clientOptions(ClientOptions.builder().socketOptions(
                        SocketOptions.builder().connectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT)).build()).build())
                .build();

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(HOST, PORT);
        redisConfiguration.setPassword(RedisPassword.of(PASSWORD));

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, clientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }
}
