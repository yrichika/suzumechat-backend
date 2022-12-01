package com.example.suzumechat.service.guest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.dto.message.ChatMessageCapsule;
import com.example.suzumechat.service.channel.dto.message.error.ChatError;
import com.example.suzumechat.service.guest.application.GuestMessageHandler;
import com.example.suzumechat.service.guest.exception.GuestIdMissingInSessionException;
import com.example.suzumechat.utility.JsonHelper;
import com.example.suzumechat.utility.dto.message.Terminate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@RestController
public class WebSocketGuestMessageController {

    @Autowired
    private GuestMessageHandler service;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JsonHelper jsonHelper;

    final String SESSION_GUEST_ID = "guestId";
    final String DEST_HOST = "/receive/host/";
    final String DEST_GUEST = "/receive/guest/";

    @MessageMapping("/guest/{guestChannelToken}") // ==
                                                  // `/send/guest/{guestChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("guestChannelToken") final String guestChannelToken,
        @Payload final String messageJson,
        final SimpMessageHeaderAccessor headerAccessor) throws Exception {
        val guestId = headerAccessor.getSessionAttributes().get(SESSION_GUEST_ID)
            .toString();
        if (guestId == null) {
            throw new GuestIdMissingInSessionException();
        }

        if (jsonHelper.hasAllFieldsOf(messageJson, ChatMessageCapsule.class)) {
            val hostChannelTokenOpt =
                service.getHostChannelToken(guestId, guestChannelToken);
            if (hostChannelTokenOpt.isPresent()) {
                broadcastToChatChannel(guestChannelToken, hostChannelTokenOpt.get(),
                    messageJson);
            }
        } else if (jsonHelper.hasAllFieldsOf(messageJson, Terminate.class)) {
            toGuest(guestChannelToken, messageJson);
        } else {
            returningToGuest(guestChannelToken);
        }
    }

    // REFACTOR: exactly the same as the host side
    private void broadcastToChatChannel(final String guestChannelToken,
        final String hostChannelToken, String messageJson) {
        template.convertAndSend(DEST_HOST + hostChannelToken, messageJson);
        template.convertAndSend(DEST_GUEST + guestChannelToken, messageJson);
    }

    private void returningToGuest(String guestChannelToken) {
        toGuest(guestChannelToken, new ChatError());
    }

    private void toGuest(String guestChannelToken, Object payload) {
        template.convertAndSend(DEST_GUEST + guestChannelToken, payload);
    }

}
