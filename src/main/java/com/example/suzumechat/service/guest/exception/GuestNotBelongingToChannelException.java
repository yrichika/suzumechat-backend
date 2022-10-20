package com.example.suzumechat.service.guest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "Guest does not belong to this channel")
public class GuestNotBelongingToChannelException extends RuntimeException {

}
