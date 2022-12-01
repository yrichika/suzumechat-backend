package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import com.example.suzumechat.service.guest.dto.PendedJoinRequestResult;

public interface VisitorMessageHandler {

    public Optional<PendedJoinRequestResult> createGuestAsVisitor(
        final String joinChannelToken, final String visitorId,
        final String codename, final String passphrase);
}
