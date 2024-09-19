package com.it.template.api.configuration;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class NettyConfiguration {

    @Value("${app.api.request-timeout:30}")
    private Long responseTimeout;

    private static final Integer NETTY_CONNECT_TIMEOUT_MILLIS = 10000;
    private static final Integer NETTY_TCP_KEEPCNT = 10;
    private static final Integer NETTY_TCP_KEEPIDLE = 90;
    private static final Integer NETTY_TCP_KEEPINTVL = 60;
    private static final Integer NETTY_SO_BACKLOG = 2048;

    private static final Integer PROVIDER_MAX_CONNECTIONS = 2000;
    private static final Integer PROVIDER_ACQUIRE_TIMEOUT = 60;

    private static final Integer REACTOR_CONNECT_TIMEOUT_MILLIS = 10000;

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(server ->
                server.childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, NETTY_CONNECT_TIMEOUT_MILLIS)
                        .childOption(EpollChannelOption.TCP_KEEPCNT, NETTY_TCP_KEEPCNT)
                        .childOption(EpollChannelOption.TCP_KEEPIDLE, NETTY_TCP_KEEPIDLE)
                        .childOption(EpollChannelOption.TCP_KEEPINTVL, NETTY_TCP_KEEPINTVL)
                        .option(ChannelOption.SO_BACKLOG, NETTY_SO_BACKLOG));

        return factory;
    }

    @Bean
    public ReactorResourceFactory reactorResourceFactory(NioEventLoopGroup nioEventLoopGroup) {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setConnectionProvider(ConnectionProvider.builder("fixed")
                .maxConnections(PROVIDER_MAX_CONNECTIONS)
                .pendingAcquireTimeout(Duration.ofSeconds(PROVIDER_ACQUIRE_TIMEOUT))
                .build());
        factory.setLoopResources(useNative -> nioEventLoopGroup);
        factory.setUseGlobalResources(false);
        return factory;
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory reactorResourceFactory) {
        return new ReactorClientHttpConnector(reactorResourceFactory, m ->
                m.followRedirect(true)
                        .responseTimeout(Duration.ofSeconds(responseTimeout))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, REACTOR_CONNECT_TIMEOUT_MILLIS));
    }
}
