package com.example.suzumechat.service.channel.controller;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.application.messagehandler.ChatMessageUseCase;
import com.example.suzumechat.service.channel.application.messagehandler.CloseJoinRequestUseCase;
import com.example.suzumechat.service.channel.application.messagehandler.HostMessageHandler;
import com.example.suzumechat.service.channel.application.messagehandler.HostUnhandledUseCase;
import com.example.suzumechat.service.channel.application.messagehandler.TerminateUseCase;
import com.example.suzumechat.service.channel.application.messagehandler.VisitorAuthStatusUseCase;
import com.example.suzumechat.service.channel.dto.message.ChatMessageCapsule;
import com.example.suzumechat.service.channel.dto.message.CloseJoinRequest;
import com.example.suzumechat.service.channel.dto.message.VisitorAuthStatus;
import com.example.suzumechat.service.channel.exception.HostIdMissingInSessionException;
import com.example.suzumechat.utility.JsonHelper;
import com.example.suzumechat.utility.dto.message.Terminate;
import lombok.val;

@RestController
public class WebSocketMessageController {

    @Autowired
    ChatMessageUseCase chatMessageUseCase;
    @Autowired
    CloseJoinRequestUseCase closeJoinRequestUseCase;
    @Autowired
    TerminateUseCase terminateUseCase;
    @Autowired
    VisitorAuthStatusUseCase visitorAuthStatusUseCase;

    @Autowired
    HostUnhandledUseCase unhandledUseCase;

    @Autowired
    private JsonHelper jsonHelper;

    private Map<Class<?>, HostMessageHandler> messageHandlers;

    @PostConstruct
    private void setMessageHandlers() {
        messageHandlers = new HashMap<Class<?>, HostMessageHandler>() {
            {
                put(ChatMessageCapsule.class, chatMessageUseCase);
                put(VisitorAuthStatus.class, visitorAuthStatusUseCase);
                put(CloseJoinRequest.class, closeJoinRequestUseCase);
                put(Terminate.class, terminateUseCase);
            }
        };
    }

    /**
     * 
     * @param hostChannelToken
     * @param message this json object string could be any type of object
     * @param headerAccessor To intercept HttpSession during WebSocket connection
     * @throws Exception
     */
    @MessageMapping("/host/{hostChannelToken}") // == `/send/host/{hostChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("hostChannelToken") final String hostChannelToken,
        @Payload final String messageJson,
        final SimpMessageHeaderAccessor headerAccessor) throws Exception {

        val hostId = headerAccessor.getSessionAttributes().get("hostId").toString();
        if (hostId == null) {
            throw new HostIdMissingInSessionException();
        }

        for (Map.Entry<Class<?>, HostMessageHandler> entry : messageHandlers.entrySet()) {
            if (jsonHelper.hasAllFieldsOf(messageJson, entry.getKey())) {
                entry.getValue().handle(hostId, hostChannelToken, messageJson);
                return;
            }
        }
        unhandledUseCase.handle(hostId, hostChannelToken, messageJson);
    }
}

