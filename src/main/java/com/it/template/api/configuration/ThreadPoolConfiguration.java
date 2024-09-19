package com.it.template.api.configuration;

import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfiguration {

    private static final Integer FACTORY_THREAD_NUMBER = 10;

    @Bean
    public NioEventLoopGroup nioEventLoopGroup() {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("ApiWebClientThread-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        return new NioEventLoopGroup(FACTORY_THREAD_NUMBER, factory);
    }
}
