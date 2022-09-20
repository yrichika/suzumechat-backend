package com.example.suzumechat.service.guest.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.guest.GuestService;
import com.example.suzumechat.service.guest.dto.AuthenticationStatus;
import com.example.suzumechat.service.guest.exception.VisitorInvalidException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
public class AuthenticationStatusStreamingController {

    @Autowired
    private GuestService service;

    @Autowired
    private HttpSession session;
    @Autowired
    Environment env;

    @GetMapping(path = "/visitor/joinStatus/{joinChannelToken:.+}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<AuthenticationStatus>> joinStatus(
            @PathVariable("joinChannelToken") final String joinChannelToken)
            throws Exception {

        val visitorId = (String) session.getAttribute("visitorId");
        if (visitorId == null) {
            throw new VisitorInvalidException();
        }

        val intervalString = env.getProperty(
                "authentication-status-streaming-interval-in-millisecond");
        val interval = Long.parseLong(intervalString);
        return Flux.interval(Duration.ofMillis(interval)).map(sequence -> {
            try {
                return ServerSentEvent.<AuthenticationStatus>builder()
                        .id(String.valueOf(sequence)).event("message")
                        .data(service.getAuthenticationStatus(joinChannelToken,
                                visitorId))
                        .build();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

}
