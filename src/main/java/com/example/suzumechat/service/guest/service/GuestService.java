package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;

public interface GuestService {

    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken)
            throws Exception;

    // originally ClientLoginRequests.create()
    public Optional<String> createGuestAsVisitor(String joinChannelToken,
            String visitorId, String codename, String passphrase) throws Exception;

    // originally ClientLoginRequests.updateStatus
    public void updateStatus(String visitorId, Boolean isAuthenticated);
}
