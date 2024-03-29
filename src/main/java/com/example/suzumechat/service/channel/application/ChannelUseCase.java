package com.example.suzumechat.service.channel.application;

import com.example.suzumechat.service.channel.dto.CreatedChannel;

public interface ChannelUseCase {
    public CreatedChannel create(String channelName, String publicKey) throws Exception;
}
