package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactivefeign.client.log.DefaultReactiveLogger;
import reactivefeign.client.log.ReactiveLoggerListener;
import reactor.core.publisher.Mono;

import java.time.Clock;

public class FeignConfig {

    private final Logger logger = LoggerFactory.getLogger(FeignConfig.class);


    @Bean
    public ReactiveLoggerListener reactiveLoggerListener() {
        return new DefaultReactiveLogger(Clock.systemUTC());
    }

    @Bean
    @Scope("prototype")
    @Primary
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> Mono.just(ClientRequest.from(request)
                        .headers(httpHeaders -> httpHeaders.set("some-header", "some-header-value"))
                        .build())))
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    logger.debug("[OUT] Headers: {}", request.headers());
                    return Mono.just(request);
                }));
    }

}
