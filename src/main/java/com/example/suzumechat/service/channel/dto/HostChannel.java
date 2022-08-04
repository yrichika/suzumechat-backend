package com.example.suzumechat.service.channel.dto;

import lombok.*;

@Value
@Builder(toBuilder=true)
@AllArgsConstructor
public class HostChannel {
    private final String hostChannelToken;
    private final String loginChannelToken;
    private final String secretKey;
}
