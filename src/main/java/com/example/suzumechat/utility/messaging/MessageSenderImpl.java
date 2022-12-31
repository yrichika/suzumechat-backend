package com.example.suzumechat.utility.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.example.suzumechat.utility.dto.message.ErrorMessage;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageSenderImpl implements MessageSender {

    @Autowired
    protected SimpMessagingTemplate template;

    // FIXME: get from config
    final String hostEndpointPrefix = "/receive/host/";
    final String guestEndpointPrefix = "/receive/guest/";
    final String visitorEndpointPrefix = "/receive/visitor/";

    @Override
    public void toHost(final String hostChannelToken, final Object message) {
        template.convertAndSend(hostEndpointPrefix + hostChannelToken, message);
    }

    @Override
    public void toVisitor(
        final String joinChannelToken,
        final String visitorId,
        final Object message) {
        val visitorReceivingUrl = visitorEndpointPrefix + joinChannelToken + "/" + visitorId;
        template.convertAndSend(visitorReceivingUrl, message);
    }

    @Override
    public void toGuest(final String guestChannelToken, final Object message) {
        template.convertAndSend(guestEndpointPrefix + guestChannelToken, message);
    }

    @Override
    public void broadcastToChat(
        final String hostChannelToken,
        final String guestChannelToken,
        final String messageJson) {
        toHost(hostChannelToken, messageJson);
        toGuest(guestChannelToken, messageJson);
    }

    @Override
    public void returningToHost(final String hostChannelToken, final ErrorMessage errorMessage) {
        log.warn("Error message from host: " + errorMessage.getType());
        toHost(hostChannelToken, errorMessage);
    }

    @Override
    public void returningToVisitor(
        final String joinChannelToken,
        final String visitorId,
        final ErrorMessage errorMessage) {
        log.warn("Error message from visitor: " + errorMessage.getType());
        toVisitor(joinChannelToken, visitorId, errorMessage);
    }

    @Override
    public void returningToGuest(final String guestChannelToken, final ErrorMessage errorMessage) {
        log.warn("Error message from guest: " + errorMessage.getType());
        toGuest(guestChannelToken, errorMessage);
    }
}
