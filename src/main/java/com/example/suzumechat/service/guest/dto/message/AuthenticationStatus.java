package com.example.suzumechat.service.guest.dto.message;

import javax.annotation.Nullable;

public record AuthenticationStatus(boolean isClosed,
        @Nullable Boolean isAuthenticated, String guestId,
        String guestChannelToken) {
}
