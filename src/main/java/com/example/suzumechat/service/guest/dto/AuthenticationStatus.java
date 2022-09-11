package com.example.suzumechat.service.guest.dto;

import javax.annotation.Nullable;

public record AuthenticationStatus(
    boolean isClosed,
    @Nullable Boolean isAuthenticated,
    String guestChannelToken
) {}
