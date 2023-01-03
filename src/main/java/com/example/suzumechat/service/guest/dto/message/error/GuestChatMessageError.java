package com.example.suzumechat.service.guest.dto.message.error;

import com.example.suzumechat.utility.dto.message.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GuestChatMessageError implements ErrorMessage {
    private String jsonMessage;
}


