package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@Service
public class GuestMessageHandlerImpl implements GuestMessageHandler {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;
    @Autowired
    private Crypter crypter;


    @Cacheable(value = "hostChannelToken")
    @Override
    public Optional<String> getHostChannelToken(final String guestId,
        final String guestChannelToken) throws Exception {
        try {
            val channel = channelService.getByGuestChannelToken(guestChannelToken);
            val guest = guestService.getByGuestId(guestId);
            if (!channel.getChannelId().equals(guest.getChannelId())) {
                return Optional.empty();
            }

            val hostChannelToken = crypter.decrypt(channel.getHostChannelTokenEnc(),
                channel.getChannelId());
            return Optional.of(hostChannelToken);
        } catch (Exception exception) {
            // TODO: log
            return Optional.empty();
        }
    }
}
