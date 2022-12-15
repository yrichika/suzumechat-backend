package com.example.suzumechat.service.channel.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;

@Service
public class HostUseCaseImpl implements HostUseCase {
    @Autowired
    private ChannelService channelService;

    @Override
    public void closeJoinRequest(String hostId, String hostChannelToke) throws Exception {
        channelService.trashSecretKeyByHostChannelToken(hostId, hostChannelToke);
    }
}
