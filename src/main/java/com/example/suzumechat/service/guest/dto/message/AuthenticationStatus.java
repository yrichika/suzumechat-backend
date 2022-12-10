package com.example.suzumechat.service.guest.dto.message;

import javax.annotation.Nullable;

public record AuthenticationStatus(
    boolean isClosed,
    @Nullable Boolean isAuthenticated,
    String guestId,
    String guestChannelToken,
    String channelName, // DELETE: maybe not necessary for visitor. without this, visitor can get
                        // channelName
    String secretKey) {
}
