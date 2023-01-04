package com.example.suzumechat.service.channel.service;

import java.util.Optional;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;

// REFACTOR: rename
public interface HostMessagingService {

    public Optional<String> getGuestChannelToken(final String hostId, final String hostChannelToken);

    public Optional<ApprovalResult> handleApproval(
        final String hostId,
        final String hostChannelToken,
        final String visitorId,
        final boolean isAuthenticated);

    public JoinRequestClosedNotification closeJoinRequest(
        final String hostId,
        final String hostChannelToken) throws Exception;
}
