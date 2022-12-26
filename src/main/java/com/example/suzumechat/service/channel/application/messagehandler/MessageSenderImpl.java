package com.example.suzumechat.service.channel.application.messagehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.example.suzumechat.utility.dto.message.ErrorMessage;
import lombok.val;

@Component
public class MessageSenderImpl implements MessageSender {

    @Autowired
    protected SimpMessagingTemplate template;

    // FIXME: get from config
    final String hostEndpointPrefix = "/receive/host/";
    final String guestEndpointPrefix = "/receive/guest/";

    @Override
    public void returningToHost(String hostChannelToken, ErrorMessage errorMessage) {
        template.convertAndSend(hostEndpointPrefix + hostChannelToken, errorMessage);
    }

    @Override
    public void toVisitor(
        String joinChannelToken,
        String visitorId,
        String json) {
        val visitorReceivingUrl = String.join("/", "/receive", "visitor",
            joinChannelToken, visitorId);
        template.convertAndSend(visitorReceivingUrl, json);
    }

    @Override
    public void toGuest(final String guestChannelToken, final String json) {
        template.convertAndSend(guestEndpointPrefix + guestChannelToken, json);
    }

    @Override
    public void broadcastToChatChannel(
        final String hostChannelToken,
        final String guestChannelToken,
        final String messageJson) {
        template.convertAndSend(hostEndpointPrefix + hostChannelToken, messageJson);
        toGuest(guestChannelToken, messageJson);
    }
}
