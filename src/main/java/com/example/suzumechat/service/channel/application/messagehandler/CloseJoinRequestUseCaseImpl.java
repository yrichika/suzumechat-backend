package com.example.suzumechat.service.channel.application.messagehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;
import com.example.suzumechat.service.channel.service.HostMessagingService;
import com.example.suzumechat.service.guest.dto.message.JoinRequestClosed;
import com.example.suzumechat.utility.messaging.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Service
public class CloseJoinRequestUseCaseImpl implements CloseJoinRequestUseCase {

    @Autowired
    private HostMessagingService hostMessagingService;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageSender messageSender;

    @Override
    public void handle(final String hostId, final String hostChannelToken, final String messageJson) throws Exception {
        final JoinRequestClosedNotification joinRequestAlreadyClosed =
            hostMessagingService.closeJoinRequest(hostId, hostChannelToken);

        if (!joinRequestAlreadyClosed.visitorIds().isEmpty()) {
            for (String visitorId : joinRequestAlreadyClosed.visitorIds()) {
                val joinRequestClosedMessage = mapper.writeValueAsString(
                    new JoinRequestClosed(true));

                messageSender.toVisitor(
                    joinRequestAlreadyClosed.joinChannelToken(),
                    visitorId,
                    joinRequestClosedMessage);
            }
        }
    }
}
