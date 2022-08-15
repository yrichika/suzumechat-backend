package com.example.suzumechat.service.channel.controller;

import java.time.Duration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
public class VisitorsStatusStreamingController {

    @Autowired
    ChannelService service;
    @Autowired
    HttpSession session;

    @GetMapping(path = "/host/requestStatus/{hostChannelToken:.+}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<VisitorsStatus>>> fetch(
        @PathVariable("hostChannelToken")
        final String hostChannelToken
    ) throws Exception {

        // WORKING: コメントちゃんとすること
        // SSE uses different session because its URL is different from when created channel (it's direct URL access,
        // not using `back` prefixed Next proxy).
        // Because it's different session, you can't use `session.getAttribute("hostId");`
        // That's why it's getting channel data from hostChannelToken in the URL.
        final String channelId = service.getByHostChannelToken(hostChannelToken).getChannelId();

        // FIXME: read interval seconds from settings.properties.
        //        then change it when testing. Testing is too slow right now for this interval.
        return Flux.interval(Duration.ofSeconds(4))
            .map(sequence -> {
                try {
                    return ServerSentEvent.<List<VisitorsStatus>> builder()
                    .id(String.valueOf(sequence))
                    .event("message")
                    .data(service.getVisitorsStatus(channelId))
                    .build();
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }

            });

    }
}
