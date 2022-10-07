package com.example.suzumechat.service.channel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "The host tokens do not match")
public class HostChannelTokensMismatchException extends RuntimeException {

}
