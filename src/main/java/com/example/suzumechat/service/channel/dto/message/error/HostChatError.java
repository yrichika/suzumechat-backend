package com.example.suzumechat.service.channel.dto.message.error;

import com.example.suzumechat.utility.dto.message.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class HostChatError implements ErrorMessage {
    private String jsonMessage;
}
