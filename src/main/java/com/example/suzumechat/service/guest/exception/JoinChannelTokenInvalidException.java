package com.example.suzumechat.service.guest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "this join channel token is invalid")
public class JoinChannelTokenInvalidException extends RuntimeException {
    
}
