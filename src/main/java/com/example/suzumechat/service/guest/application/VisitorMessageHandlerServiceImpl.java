package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.PendedJoinRequestResult;
import lombok.val;
import com.example.suzumechat.service.guest.dto.message.ManagedJoinRequest;
import com.example.suzumechat.service.guest.service.GuestService;

@Service
public class VisitorMessageHandlerServiceImpl
        implements VisitorMessageHandlerService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;

    @Override
    public Optional<PendedJoinRequestResult> createGuestAsVisitor(
            String joinChannelToken, String visitorId, String codename,
            String passphrase) {

        try {
            guestService.createGuestAsVisitor(joinChannelToken, visitorId, codename,
                    passphrase);

            val hostChannelToken = channelService
                    .getHostChannelTokenByJoinChannelToken(joinChannelToken);

            val managedJoinRequest = new ManagedJoinRequest(visitorId, codename,
                    passphrase, Optional.empty());
            val result = new PendedJoinRequestResult(hostChannelToken,
                    managedJoinRequest);
            return Optional.of(result);
        } catch (Exception exception) {
            // TODO: log
            return Optional.empty();
        }
    }
}
