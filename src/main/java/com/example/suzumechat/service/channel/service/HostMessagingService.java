package com.example.suzumechat.service.channel.service;

import java.util.Optional;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;

// REFACTOR: rename
public interface HostMessagingService {

    public Optional<String> getGuestChannelToken(String hostId, String hostChannelToken);

    public Optional<ApprovalResult> handleApproval(
        String hostId,
        String hostChannelToken,
        String visitorId,
        boolean isAuthenticated);

    public JoinRequestClosedNotification closeJoinRequest(
        String hostId,
        String hostChannelToken) throws Exception;
}
