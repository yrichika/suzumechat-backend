package com.example.suzumechat.service.channel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "Channel was not found by the given token")
public class ChannelNotFoundByTokenException extends RuntimeException {

}
