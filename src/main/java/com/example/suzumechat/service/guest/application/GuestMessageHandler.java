package com.example.suzumechat.service.guest.application;

import java.util.Optional;

public interface GuestMessageHandler {
    public Optional<String> getHostChannelToken(final String guestId,
        final String guestChannelToken) throws Exception;
}