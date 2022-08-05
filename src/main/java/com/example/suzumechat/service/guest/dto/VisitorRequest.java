package com.example.suzumechat.service.guest.dto;

import java.util.Optional;

import lombok.*;

// originally `CreatedRequest`
@Value
@AllArgsConstructor
public class VisitorRequest {
    private String visitorId;
    private String codename;
    private String passphrase;
    private Optional<Boolean> isAuthenticated;
}
