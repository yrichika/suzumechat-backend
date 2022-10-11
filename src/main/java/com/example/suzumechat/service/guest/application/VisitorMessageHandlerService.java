package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.PendedJoinRequestResult;

public interface VisitorMessageHandlerService {

    public PendedJoinRequestResult createGuestAsVisitor(
            final String joinChannelToken, final String visitorId,
            final String codename, final String passphrase) throws Exception;
}
