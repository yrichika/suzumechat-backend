package com.example.suzumechat.service.guest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "guest id is missing in the session")
public class GuestIdMissingInSessionException extends RuntimeException {

}
