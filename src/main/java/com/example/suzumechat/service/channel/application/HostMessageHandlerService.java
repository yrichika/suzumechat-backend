package com.example.suzumechat.service.channel.application;

import com.example.suzumechat.service.channel.dto.ApprovalResult;

public interface HostMessageHandlerService {

    public String getGuestChannelToken(final String hostId,
            final String hostChannelToken) throws Exception;

    public ApprovalResult handleApproval(final String hostId,
            final String hostChannelToken, final String visitorId,
            final boolean isAuthenticated) throws Exception;
}
