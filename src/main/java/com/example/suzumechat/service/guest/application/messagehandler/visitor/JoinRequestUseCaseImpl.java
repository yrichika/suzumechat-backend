package com.example.suzumechat.service.guest.application.messagehandler.visitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.service.guest.dto.message.JoinRequestClosed;
import com.example.suzumechat.service.guest.dto.message.error.JoinRequestError;
import com.example.suzumechat.service.guest.service.VisitorMessagingService;
import com.example.suzumechat.service.valueobject.ChannelToken;
import com.example.suzumechat.utility.messaging.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Service
public class JoinRequestUseCaseImpl implements JoinRequestUseCase {
    @Autowired
    private VisitorMessagingService messagingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageSender messageSender;

    @Override
    public void handle(final String joinChannelToken, final String messageJson) throws Exception {

        final JoinRequest joinRequest = mapper.readValue(messageJson, JoinRequest.class);

        val hostChannelTokenOpt = messagingService.createGuestAsVisitor(
            joinChannelToken,
            joinRequest.visitorId(),
            joinRequest.visitorPublicKey(),
            joinRequest.whoIAmEnc());
        if (hostChannelTokenOpt.isPresent()) {
            if (hostChannelTokenOpt.get() instanceof ChannelToken) {
                messageSender.toHost(hostChannelTokenOpt.get().value(), joinRequest);
            } else {
                messageSender.toVisitor(
                    joinChannelToken,
                    joinRequest.visitorId(),
                    new JoinRequestClosed(true));
            }
        } else {
            messageSender.returningToVisitor(
                joinChannelToken,
                joinRequest.visitorId(),
                new JoinRequestError());
        }
    }
}
