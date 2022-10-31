package com.example.suzumechat.service.channel.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.service.ChannelService;

@Service
public class ChannelUseCaseImpl implements ChannelUseCase {
    @Autowired
    private ChannelService service;

    @Override
    public CreatedChannel create(String channelName) throws Exception {
        final CreatedChannel channel = service.create(channelName);
        return channel;
    }
}
