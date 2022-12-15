package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;

public interface VisitorMessageHandler {

    public Optional<VisitorHandlingStringType> createGuestAsVisitor(
        final String joinChannelToken,
        final String visitorId,
        final String visitorPublicKey,
        final String whoIAmEnc);
}
