package com.example.suzumechat.service.channel.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController {

    @Autowired
    private ChannelService service;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 
     * @param hostChannelToken
     * @param messageEnc This message(string) MUST be encrypted at frontend. This method just receives any string messages and broadcasts to the host and the guest channel.
     * @param headerAccessor To intercept HttpSession during WebSocket connection
     * @throws Exception
     */
    @MessageMapping("/host/{hostChannelToken}") // `/send/host/{hostChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("hostChannelToken") final String hostChannelToken,
        @Payload final String messageEnc,
        SimpMessageHeaderAccessor headerAccessor
    ) throws Exception {
        val hostId = headerAccessor.getSessionAttributes().get("hostId").toString();

        if (hostId == null) {
            throw new HostUnauthorizedException();
        }
    
        val guestChannelToken = service.getGuestChannelToken(hostId, hostChannelToken);

        template.convertAndSend("/receive/host/" + hostChannelToken, messageEnc);
        template.convertAndSend("/receive/guest/" + guestChannelToken, messageEnc);

    }
}
