package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;

public interface VisitorMessagingService {

    public Optional<VisitorHandlingStringType> createGuestAsVisitor(
        String joinChannelToken,
        String visitorId,
        String visitorPublicKey,
        String whoIAmEnc);
}
