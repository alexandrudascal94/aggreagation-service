package com.tnt.aggregationservice.config;

import io.netty.handler.logging.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ClientConfig clientConfig;

    @Bean
    public WebClient transactionServiceWebClient() {
        return getWebClient(clientConfig.getConnectionTimeout(), clientConfig.getBaseUrl(), "aggregation-service");
    }

    private WebClient getWebClient(Integer connectTimeout, String baseUrl, String connectionName) {
        ConnectionProvider connectionProvider = ConnectionProvider
                .builder(connectionName)
                .maxIdleTime(Duration.ofMinutes(3))
                .maxConnections(200)
                .build();

        var httpClient = HttpClient.create(connectionProvider)
                .option(CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        var connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(connector)
                .baseUrl(baseUrl)
                .build();
    }
}
