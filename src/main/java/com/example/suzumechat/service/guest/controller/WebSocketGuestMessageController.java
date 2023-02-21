package com.example.suzumechat.service.guest.controller;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.dto.message.ChatMessageCapsule;
import com.example.suzumechat.service.guest.application.messagehandler.guest.GuestChatMessageUseCase;
import com.example.suzumechat.service.guest.application.messagehandler.guest.GuestMessageHandler;
import com.example.suzumechat.service.guest.application.messagehandler.guest.GuestUnhandledUseCase;
import com.example.suzumechat.service.guest.exception.GuestIdMissingInSessionException;
import com.example.suzumechat.utility.JsonHelper;
import lombok.val;

@RestController
public class WebSocketGuestMessageController {

    @Autowired
    private GuestChatMessageUseCase chatMessageUseCase;

    @Autowired
    private GuestUnhandledUseCase unhandledUseCase;

    @Autowired
    private JsonHelper jsonHelper;

    public final String SESSION_GUEST_ID = "guestId";

    private Map<Class<?>, GuestMessageHandler> messageHandlers;

    @PostConstruct
    private void setMessageHandlers() {
        messageHandlers = new HashMap<Class<?>, GuestMessageHandler>() {
            {
                put(ChatMessageCapsule.class, chatMessageUseCase);
            }
        };
    }

    @MessageMapping("/guest/{guestChannelToken}") // == `/send/guest/{guestChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("guestChannelToken") final String guestChannelToken,
        @Payload final String messageJson,
        final SimpMessageHeaderAccessor headerAccessor) throws Exception {
        val guestId = headerAccessor.getSessionAttributes().get(SESSION_GUEST_ID)
            .toString();
        if (guestId == null) {
            throw new GuestIdMissingInSessionException();
        }

        for (Map.Entry<Class<?>, GuestMessageHandler> entry : messageHandlers.entrySet()) {
            if (jsonHelper.hasAllFieldsOf(messageJson, entry.getKey())) {
                entry.getValue().handle(guestId, guestChannelToken, messageJson);
                return;
            }
        }
        unhandledUseCase.handle(guestId, guestChannelToken, messageJson);
    }
}
