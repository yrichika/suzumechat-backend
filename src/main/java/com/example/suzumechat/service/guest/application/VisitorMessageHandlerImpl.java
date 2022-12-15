package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.service.valueobject.ChannelToken;
import com.example.suzumechat.service.valueobject.EmptyChannelToken;
import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;
import lombok.val;

@Service
public class VisitorMessageHandlerImpl
    implements VisitorMessageHandler {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;

    @Override
    public Optional<VisitorHandlingStringType> createGuestAsVisitor(
        final String joinChannelToken,
        final String visitorId,
        final String visitorPublicKey,
        final String whoIAmEnc) {

        try {
            final Channel channel = channelService.getByJoinChannelToken(joinChannelToken);
            if (channel.isClosed()) {
                return Optional.of(new EmptyChannelToken());
            }
            guestService.createGuestAsVisitor(joinChannelToken, visitorId, channel);

            val hostChannelTokenString = channelService
                .getHostChannelTokenByJoinChannelToken(joinChannelToken);
            final ChannelToken hostChannelToken = new ChannelToken(hostChannelTokenString);
            return Optional.of(hostChannelToken);
        } catch (Exception exception) {
            // TODO: log
            return Optional.empty();
        }
    }
}
