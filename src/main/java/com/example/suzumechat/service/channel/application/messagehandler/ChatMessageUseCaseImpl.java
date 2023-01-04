package com.example.suzumechat.service.channel.application.messagehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.message.error.HostChatError;
import com.example.suzumechat.service.channel.service.HostService;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@Service
public class ChatMessageUseCaseImpl implements ChatMessageUseCase {

    @Autowired
    HostService hostService;
    @Autowired
    MessageSender messageSender;

    @Override
    public void handle(
        final String hostId,
        final String hostChannelToken,
        final String messageJson) throws Exception {

        val guestChannelTokenOpt =
            hostService.getGuestChannelToken(hostId, hostChannelToken);

        if (guestChannelTokenOpt.isPresent()) {
            messageSender.broadcastToChat(hostChannelToken, guestChannelTokenOpt.get(),
                messageJson);
        } else {
            messageSender.returningToHost(hostChannelToken, new HostChatError(messageJson));
        }
    }

}
