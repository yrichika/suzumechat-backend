package com.example.suzumechat.service.channel.application.messagehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.message.error.TerminationError;
import com.example.suzumechat.service.channel.service.HostMessagingService;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@Service
public class TerminateUseCaseImpl implements TerminateUseCase {
    @Autowired
    HostMessagingService hostMessagingService;
    @Autowired
    MessageSender messageSender;

    @Override
    public void handle(final String hostId, final String hostChannelToken, final String messageJson) throws Exception {
        // REFACTOR: almost the same as when received ChatMessageCapsule
        val guestChannelTokenOpt =
            hostMessagingService.getGuestChannelToken(hostId, hostChannelToken);

        if (guestChannelTokenOpt.isPresent()) {
            messageSender.toGuest(guestChannelTokenOpt.get(), messageJson);
        } else {
            // FIXME: maybe not useful? host can't be notified?
            messageSender.returningToHost(hostChannelToken, new TerminationError());
        }
    }
}
