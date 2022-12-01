package com.example.suzumechat.service.guest.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@Service
public class VisitorUseCaseImpl implements VisitorUseCase {

    @Autowired
    private ChannelService service;

    @Autowired
    private Crypter crypter;

    @Override
    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken)
        throws Exception {
        final Channel channel = service.getByJoinChannelToken(joinChannelToken);

        val channelName =
            crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId());

        if (channel.getSecretKeyEnc() == null) {
            return new ChannelStatus(channelName, false);
        }

        return new ChannelStatus(channelName, true);
    }
}
