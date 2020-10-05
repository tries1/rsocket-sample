package com.example.rsocketsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@SpringBootApplication
public class RsocketSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsocketSampleApplication.class, args);
    }

    @MessageMapping("request-response")
    Mono<Message> requestResponse(Message message) {
        log.info("Receiverd request-response : {}", message);
        return Mono.just(new Message("You said: " + message.getMessage()));
    }

    @MessageMapping("fire-and-forget")
    Mono<Message> fireAndForget(Message message) {
        log.info("Receiverd fire-and-forget : {}", message);
        return Mono.empty();
    }

    @MessageMapping("request-stream")
    Flux<Message> requestStream(Message message) {
        log.info("Receiverd stream : {}", message);
        return Flux
                .interval(Duration.ofSeconds(1))
                .map(index -> new Message("You said: " + message.getMessage() + ". Response #" + index))
                .log();
    }

    @MessageMapping("stream-stream")
    Flux<Message> streamStream(Flux<Integer> settings) {
        log.info("Receiverd stream-stream (channel) request...");
        return settings
                .doOnNext(setting -> log.info("Requested interval is {} seconds.", setting.intValue()))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .switchMap(setting -> Flux.interval(Duration.ofSeconds(setting))
                        .map(index -> new Message("Stream Response #" + index))
                )
                .log();

    }
}
