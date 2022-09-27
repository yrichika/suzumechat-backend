package com.example.suzumechat.service.channel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND,
        reason = "This visitor doesn't or no longer exists")
public class VisitorNotFoundException extends RuntimeException {

}
