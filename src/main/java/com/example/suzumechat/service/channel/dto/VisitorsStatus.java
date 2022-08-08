package com.example.suzumechat.service.channel.dto;

import lombok.*;

@Value
@Builder(toBuilder=true)
@AllArgsConstructor
public class VisitorsStatus {
    String visitorId;
    String codename;
    String passphrase;
    Boolean isAuthenticated;
}
