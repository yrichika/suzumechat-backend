package com.example.suzumechat.service.guest.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.service.GuestService;
import lombok.val;

@Service
public class GuestChannelServiceImpl implements GuestChannelService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;

    public GuestChannel getGuestChannelByGuestChannelToken(
            final String guestChannelToken) throws Exception {
        val channelName =
                channelService.getChannelNameByGuestChannelToken(guestChannelToken);

        return new GuestChannel(channelName);
    }
}
