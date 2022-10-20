package com.example.suzumechat.service.guest.dto.message;

import java.util.Optional;

// originally `CreatedRequest`
public record ManagedJoinRequest(String visitorId, String codename,
        String passphrase, Optional<Boolean> isAuthenticated) {
}
