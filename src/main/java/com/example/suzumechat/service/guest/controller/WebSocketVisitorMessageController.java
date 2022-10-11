package com.example.suzumechat.service.guest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import com.example.suzumechat.service.guest.application.VisitorMessageHandlerService;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;
import com.example.suzumechat.utility.JsonHelper;

@RestController
public class WebSocketVisitorMessageController {

    @Autowired
    private VisitorMessageHandlerService service;

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

            val pendedRequest = service.createGuestAsVisitor(joinChannelToken,
                    joinRequest.visitorId(), joinRequest.codename(),
                    joinRequest.passphrase());

            sendToHost(pendedRequest.hostChannelToken(),
                    pendedRequest.visitorsRequest());
        }
    }

    private void sendToHost(String hostChannelToken,
            VisitorsRequest authenticationStatus) {
        val hostReceivingUrl = String.join("/", "/receive/host", hostChannelToken);

        template.convertAndSend(hostReceivingUrl, authenticationStatus);
    }
}