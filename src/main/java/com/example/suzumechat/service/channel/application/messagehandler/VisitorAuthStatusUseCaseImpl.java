package com.example.suzumechat.service.channel.application.messagehandler;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.dto.message.VisitorAuthStatus;
import com.example.suzumechat.service.channel.dto.message.error.ApprovalError;
import com.example.suzumechat.service.channel.service.HostMessagingService;
import com.example.suzumechat.utility.messaging.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Service
public class VisitorAuthStatusUseCaseImpl implements VisitorAuthStatusUseCase {

    @Autowired
    private HostMessagingService hostMessagingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MessageSender messageSender;

    @Override
    public void handle(final String hostId, final String hostChannelToken, final String messageJson) throws Exception {
        val visitorsAuthStatus = mapper.readValue(messageJson, VisitorAuthStatus.class);
        final Optional<ApprovalResult> approvalResultOpt = hostMessagingService.handleApproval(
            hostId,
            hostChannelToken,
            visitorsAuthStatus.visitorId(),
            visitorsAuthStatus.isAuthenticated());

        if (approvalResultOpt.isPresent()) {
            val approvalResult = approvalResultOpt.get();
            val returnMessage = mapper
                .writeValueAsString(approvalResult.authenticationStatus());

            messageSender.toVisitor(
                approvalResult.joinChannelToken(),
                visitorsAuthStatus.visitorId(),
                returnMessage);

        } else {
            messageSender.returningToHost(hostChannelToken, new ApprovalError());
        }
    }
}
