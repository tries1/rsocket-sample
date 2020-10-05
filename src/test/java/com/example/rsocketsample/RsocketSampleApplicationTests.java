package com.example.rsocketsample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RsocketSampleApplicationTests {

    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder,
                                 @LocalRSocketServerPort Integer port,
                                 @Autowired RSocketStrategies strategies) {

        requester = builder
                .connectTcp("localhost", port)
                .block();

    }

    @Test
    public void testRequestGetsResponse() {
        Mono<Message> response = requester
                .route("request-response")
                .data(new Message("TEST"))
                .retrieveMono(Message.class);

        StepVerifier
                .create(response)
                .consumeNextWith(message -> Assertions.assertEquals("You said: TEST", message.getMessage()))
                .verifyComplete();
    }

    @Test
    public void testFireAndForget() {
        Mono<Void> result = requester
                .route("fire-and-forget")
                .data(new Message("TEST"))
                .retrieveMono(Void.class);

        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    public void testRequestStream() {
        Flux<Message> stream = requester
                .route("request-stream")
                .data(new Message("TEST"))
                .retrieveFlux(Message.class);

        StepVerifier
                .create(stream)
                .consumeNextWith(message -> Assertions.assertEquals(message.getMessage(), "You said: TEST. Response #0"))
                .expectNextCount(0)
                .consumeNextWith(message -> Assertions.assertEquals(message.getMessage(), "You said: TEST. Response #1"))
                .thenCancel()
                .verify();
    }
}
