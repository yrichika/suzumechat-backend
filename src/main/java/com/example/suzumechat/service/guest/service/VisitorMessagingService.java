package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;

public interface VisitorMessagingService {

    public Optional<VisitorHandlingStringType> createGuestAsVisitor(
        final String joinChannelToken,
        final String visitorId,
        final String visitorPublicKey,
        final String whoIAmEnc);
}
