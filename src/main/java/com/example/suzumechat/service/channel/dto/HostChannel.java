package com.example.suzumechat.service.channel.dto;

import lombok.*;

@Value
@Builder(toBuilder=true)
@AllArgsConstructor
public class HostChannel {
    private String hostChannelToken;
    private String loginChannelToken;
    private String secretKey;
}
