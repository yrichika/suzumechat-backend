package com.example.suzumechat.service.guest.dto;

import java.util.Optional;

import lombok.*;

// originally `CreatedRequest`
@Value
@AllArgsConstructor
public class VisitorRequest {
    @NonNull
    private String visitorId;
    @NonNull
    private String codename;
    @NonNull
    private String passphrase;

    private Optional<Boolean> isAuthenticated;
}
