package com.example.suzumechat.service.channel.dto;

import lombok.Value;

@Value
public class CreatedChannel {
    private String hostChannelToken;
    private String loginChannelToken;
    private String secretKey;
}
