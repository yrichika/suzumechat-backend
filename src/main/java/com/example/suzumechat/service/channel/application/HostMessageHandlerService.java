package com.example.suzumechat.service.channel.application;

import java.util.Optional;
import com.example.suzumechat.service.channel.dto.ApprovalResult;

public interface HostMessageHandlerService {

    public Optional<String> getGuestChannelToken(final String hostId,
            final String hostChannelToken);

    public Optional<ApprovalResult> handleApproval(final String hostId,
            final String hostChannelToken, final String visitorId,
            final boolean isAuthenticated);
}
