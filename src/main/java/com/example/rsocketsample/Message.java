package com.example.rsocketsample;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private long created = Instant.now().getEpochSecond();

    public Message(String message) {
        this.message = message;

    }
}
