package com.example.suzumechat.service.guest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "this visitor is invalid")
public class VisitorInvalidException extends RuntimeException {

}
