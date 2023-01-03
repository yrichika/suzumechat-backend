package com.example.suzumechat.service.guest.application.messagehandler.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.guest.dto.message.error.GuestChatMessageError;
import com.example.suzumechat.service.guest.service.GuestMessagingService;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@Service
public class GuestChatMessageUseCaseImpl implements GuestChatMessageUseCase {

    @Autowired
    GuestMessagingService messagingService;
    @Autowired
    MessageSender messageSender;

    public void handle(String guestId, String guestChannelToken, String messageJson) throws Exception {
        val hostChannelTokenOpt = messagingService.getHostChannelToken(guestId, guestChannelToken);
        if (hostChannelTokenOpt.isPresent()) {
            messageSender.broadcastToChat(
                hostChannelTokenOpt.get(),
                guestChannelToken,
                messageJson);
            return;
        }

        messageSender.returningToGuest(guestChannelToken, new GuestChatMessageError(messageJson));
    }
}
