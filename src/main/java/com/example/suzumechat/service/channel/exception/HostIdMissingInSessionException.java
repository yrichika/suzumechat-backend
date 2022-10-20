package com.example.suzumechat.service.channel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "Host id is missing in the session")
public class HostIdMissingInSessionException extends RuntimeException {

}
