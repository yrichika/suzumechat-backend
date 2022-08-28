package com.example.suzumechat.service.guest.dto;

import java.util.Optional;

// originally `CreatedRequest`
public record VisitorsRequest(String visitorId, String codename, String passphrase, Optional<Boolean> isAuthenticated) {}
