package com.example.suzumechat.service.channel.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.application.HostMessageHandler;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;
import com.example.suzumechat.service.channel.dto.message.ChatMessageCapsule;
import com.example.suzumechat.service.channel.dto.message.CloseJoinRequest;
import com.example.suzumechat.service.channel.dto.message.VisitorsAuthStatus;
import com.example.suzumechat.service.channel.dto.message.error.ApprovalError;
import com.example.suzumechat.service.channel.dto.message.error.ChatError;
import com.example.suzumechat.service.channel.exception.HostIdMissingInSessionException;
import com.example.suzumechat.service.guest.dto.message.JoinRequestClosed;
import com.example.suzumechat.utility.JsonHelper;
import com.example.suzumechat.utility.dto.message.ErrorMessage;
import com.example.suzumechat.utility.dto.message.Terminate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@RestController
public class WebSocketMessageController {

    @Autowired
    private HostMessageHandler messageHandler;
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
    @MessageMapping("/host/{hostChannelToken}") // == `/send/host/{hostChannelToken}`
    public void receiveAndBroadcast(
        @DestinationVariable("hostChannelToken") final String hostChannelToken,
        @Payload final String messageJson,
        final SimpMessageHeaderAccessor headerAccessor) throws Exception {

        val hostId = headerAccessor.getSessionAttributes().get("hostId").toString();
        if (hostId == null) {
            throw new HostIdMissingInSessionException();
        }

        // REFACTOR: strategy pattern applicable here?
        // move all to UseCases
        if (jsonHelper.hasAllFieldsOf(messageJson, ChatMessageCapsule.class)) {
            val guestChannelTokenOpt =
                messageHandler.getGuestChannelToken(hostId, hostChannelToken);

            if (guestChannelTokenOpt.isPresent()) {
                broadcastToChatChannel(hostChannelToken, guestChannelTokenOpt.get(),
                    messageJson);
            } else {
                returningToHost(hostChannelToken, new ChatError());
            }
        } else if (jsonHelper.hasAllFieldsOf(messageJson,
            VisitorsAuthStatus.class)) {

            val visitorsAuthStatus =
                mapper.readValue(messageJson, VisitorsAuthStatus.class);
            final Optional<ApprovalResult> approvalResultOpt =
                messageHandler.handleApproval(hostId, hostChannelToken,
                    visitorsAuthStatus.visitorId(),
                    visitorsAuthStatus.isAuthenticated());
            if (approvalResultOpt.isPresent()) {
                val approvalResult = approvalResultOpt.get();
                val returnMessage = mapper
                    .writeValueAsString(approvalResult.authenticationStatus());

                sendToVisitor(
                    approvalResult.joinChannelToken(),
                    visitorsAuthStatus.visitorId(),
                    returnMessage);

            } else {
                returningToHost(hostChannelToken, new ApprovalError());
            }
        } else if (jsonHelper.hasAllFieldsOf(messageJson, CloseJoinRequest.class)) {

            final JoinRequestClosedNotification joinRequestAlreadyClosed =
                messageHandler.closeJoinRequest(hostId, hostChannelToken);
            if (!joinRequestAlreadyClosed.visitorIds().isEmpty()) {
                for (String visitorId : joinRequestAlreadyClosed.visitorIds()) {
                    val joinRequestClosedMessage = mapper.writeValueAsString(
                        new JoinRequestClosed(true));
                    sendToVisitor(
                        joinRequestAlreadyClosed.joinChannelToken(),
                        visitorId,
                        joinRequestClosedMessage);
                }
            }
        } else if (jsonHelper.hasAllFieldsOf(messageJson, Terminate.class)) {
            // REFACTOR: almost the same as when received ChatMessageCapsule
            val guestChannelTokenOpt =
                messageHandler.getGuestChannelToken(hostId, hostChannelToken);

            if (guestChannelTokenOpt.isPresent()) {
                toGuest(guestChannelTokenOpt.get(), messageJson);
            } else {
                returningToHost(hostChannelToken, new ChatError());
            }
        } else {
            // TODO: send ErrorMessage to host
        }
    }

    private void broadcastToChatChannel(
        final String hostChannelToken,
        final String guestChannelToken,
        final String messageJson) {
        template.convertAndSend("/receive/host/" + hostChannelToken, messageJson);
        template.convertAndSend("/receive/guest/" + guestChannelToken, messageJson);
    }

    private void sendToVisitor(
        String joinChannelToken,
        String visitorId,
        String json) {
        val visitorReceivingUrl = String.join("/", "/receive", "visitor",
            joinChannelToken, visitorId);
        template.convertAndSend(visitorReceivingUrl, json);
    }

    private void toGuest(final String guestChannelToken, final String terminateJson) {
        template.convertAndSend("/receive/guest/" + guestChannelToken, terminateJson);
    }

    private void returningToHost(String hostChannelToken,
        ErrorMessage errorMessage) {
        template.convertAndSend("/receive/host/" + hostChannelToken, errorMessage);
    }
}

