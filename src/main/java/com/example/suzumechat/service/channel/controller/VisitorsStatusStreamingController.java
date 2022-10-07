package com.example.suzumechat.service.channel.controller;

import java.time.Duration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.HostIdMissingInSessionException;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
public class VisitorsStatusStreamingController {

    @Autowired
    private ChannelService service;
    @Autowired
    private HttpSession session;
    @Autowired
    private Environment env;

    @GetMapping(path = "/host/requestStatus/{hostChannelToken:.+}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<VisitorsStatus>>> fetch(
            @PathVariable("hostChannelToken") final String hostChannelToken)
            throws Exception {

        // FIXME: SSE connection fails on load sometimes
        val hostId = (String) session.getAttribute("hostId");
        if (hostId == null) {
            throw new HostIdMissingInSessionException();
        }
        final String channelId = service
                .getByHostChannelToken(hostId, hostChannelToken).getChannelId();
        val intervalString =
                env.getProperty("visitor-status-streaming-interval-in-millisecond");
        val interval = Long.parseLong(intervalString);
        return Flux.interval(Duration.ofMillis(interval)).map(sequence -> {
            try {
                return ServerSentEvent.<List<VisitorsStatus>>builder()
                        .id(String.valueOf(sequence)).event("message")
                        .data(service.getVisitorsStatus(channelId)).build();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

        });

    }
}
