package com.example.suzumechat.utility.dto.message;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Unhandled implements ErrorMessage {
    private String jsonMessage;
}
