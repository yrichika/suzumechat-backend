package com.example.suzumechat.service.guest.service;

import java.util.Optional;

// REFACTOR: rename
public interface GuestMessagingService {
    public Optional<String> getHostChannelToken(
        final String guestId,
        final String guestChannelToken) throws Exception;
}
