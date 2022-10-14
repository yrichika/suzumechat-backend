package com.example.suzumechat.service.guest.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.dto.GuestDto;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@Service
public class GuestChannelServiceImpl implements GuestChannelService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;
    @Autowired
    private Crypter crypter;

    public GuestChannel getGuestChannelByGuestChannelToken(
            final String guestChannelToken) throws Exception {
        val channelName =
                channelService.getChannelNameByGuestChannelToken(guestChannelToken);

        return new GuestChannel(channelName);
    }

    @Override
    public GuestDto getGuestDtoByGuestId(final String guestId,
            final String guestChannelToken) throws Exception {
        val channel = channelService.getByGuestChannelToken(guestChannelToken);
        val guest = guestService.getByGuestId(guestId);
        val codename =
                crypter.decrypt(guest.getCodenameEnc(), channel.getChannelId());
        val secretKey =
                crypter.decrypt(channel.getSecretKeyEnc(), channel.getChannelId());
        return new GuestDto(codename, secretKey);
    }
}
