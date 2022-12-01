package com.example.suzumechat.service.guest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.VisitorMessageHandler;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.service.guest.dto.message.ManagedJoinRequest;
import com.example.suzumechat.service.guest.dto.message.error.JoinRequestError;
import com.example.suzumechat.utility.JsonHelper;
import com.example.suzumechat.utility.dto.message.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@RestController
public class WebSocketVisitorMessageController {

    @Autowired
    private VisitorMessageHandler service;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JsonHelper jsonHelper;

    /**
     * 
     * @param hostChannelToken
     * @param message this json object string could be any type of object
     * @param headerAccessor To intercept HttpSession during WebSocket connection
     * @throws Exception
     */
    @MessageMapping("/visitor/{joinChannelToken}") // `/send/visitor/{joinChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("joinChannelToken") final String joinChannelToken,
        @Payload final String messageJson) throws Exception {

        if (jsonHelper.hasAllFieldsOf(messageJson, JoinRequest.class)) {

            val joinRequest = mapper.readValue(messageJson, JoinRequest.class);

            val pendedRequestOpt = service.createGuestAsVisitor(joinChannelToken,
                joinRequest.visitorId(), joinRequest.codename(),
                joinRequest.passphrase());
            if (pendedRequestOpt.isPresent()) {
                sendToHost(pendedRequestOpt.get().hostChannelToken(),
                    pendedRequestOpt.get().managedJoinRequest());
            } else {
                returningToVisitor(joinChannelToken, joinRequest.visitorId(),
                    new JoinRequestError());
            }


        }
    }

    private void returningToVisitor(String joinChannelToken, String visitorId,
        ErrorMessage errorMessage) {
        // REFACTOR: TEST:
        template.convertAndSend(
            "/receive/visitor/" + joinChannelToken + "/" + visitorId,
            errorMessage);
    }

    private void sendToHost(String hostChannelToken,
        ManagedJoinRequest authenticationStatus) {
        val hostReceivingUrl = String.join("/", "/receive/host", hostChannelToken);

        template.convertAndSend(hostReceivingUrl, authenticationStatus);
    }
}
