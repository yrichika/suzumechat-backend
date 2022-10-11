package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;
import com.example.suzumechat.service.guest.form.JoinRequest;

public interface GuestService {

    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken)
            throws Exception;

    // originally ClientLoginRequests.create()
    public Optional<String> createGuestAsVisitor(String joinChannelToken,
            String visitorId, String codename, String passphrase) throws Exception;

    public AuthenticationStatus getAuthenticationStatus(String joinChannelToken,
            String visitorId) throws Exception;

    // originally ClientLoginRequests.updateStatus
    public void updateStatus(String visitorId, Boolean isAuthenticated);
}