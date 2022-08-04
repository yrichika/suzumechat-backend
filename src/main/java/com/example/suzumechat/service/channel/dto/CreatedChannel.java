package com.example.suzumechat.service.channel.dto;

import lombok.*;

@Value
@Builder(toBuilder=true)
@AllArgsConstructor
public class CreatedChannel {
    private final String hostId;
    private final HostChannel hostChannel;
}
