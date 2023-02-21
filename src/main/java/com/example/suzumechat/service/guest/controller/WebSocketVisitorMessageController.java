package com.example.suzumechat.service.guest.controller;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.messagehandler.visitor.JoinRequestUseCase;
import com.example.suzumechat.service.guest.application.messagehandler.visitor.VisitorMessageHandler;
import com.example.suzumechat.service.guest.application.messagehandler.visitor.VisitorUnhandledUseCase;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.utility.JsonHelper;

@RestController
public class WebSocketVisitorMessageController {

    @Autowired
    private JoinRequestUseCase joinRequestUseCase;

    @Autowired
    private VisitorUnhandledUseCase unhandledUseCase;

    @Autowired
    private JsonHelper jsonHelper;

    private Map<Class<?>, VisitorMessageHandler> messageHandlers;

    @PostConstruct
    private void setMessageHandlers() {
        messageHandlers = new HashMap<Class<?>, VisitorMessageHandler>() {
            {
                put(JoinRequest.class, joinRequestUseCase);
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
    @MessageMapping("/visitor/{joinChannelToken}") // `/send/visitor/{joinChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("joinChannelToken") final String joinChannelToken,
        @Payload final String messageJson) throws Exception {

        for (Map.Entry<Class<?>, VisitorMessageHandler> entry : messageHandlers.entrySet()) {
            if (jsonHelper.hasAllFieldsOf(messageJson, entry.getKey())) {
                entry.getValue().handle(joinChannelToken, messageJson);
                return;
            }
        }
        unhandledUseCase.handle(joinChannelToken, messageJson);
    }
}
