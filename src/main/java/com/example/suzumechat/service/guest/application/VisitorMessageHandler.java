package com.example.suzumechat.service.guest.application;

import java.util.Optional;

public interface VisitorMessageHandler {

    public Optional<String> createGuestAsVisitor(
        final String joinChannelToken, final String visitorId, final String visitorPublicKey,
        final String whoIAmEnc);
}
