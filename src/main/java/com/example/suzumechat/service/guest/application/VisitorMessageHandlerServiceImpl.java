package com.example.suzumechat.service.guest.application;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.PendedJoinRequestResult;
import lombok.val;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;
import com.example.suzumechat.service.guest.service.GuestService;

@Service
public class VisitorMessageHandlerServiceImpl
        implements VisitorMessageHandlerService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;

    @Override
    public PendedJoinRequestResult createGuestAsVisitor(String joinChannelToken,
            String visitorId, String codename, String passphrase) throws Exception {
        guestService.createGuestAsVisitor(joinChannelToken, visitorId, codename,
                passphrase);

        val hostChannelToken = channelService
                .getHostChannelTokenByJoinChannelToken(joinChannelToken);

        val visitorsRequest = new VisitorsRequest(visitorId, codename, passphrase,
                Optional.empty());

        return new PendedJoinRequestResult(hostChannelToken, visitorsRequest);
    }
}
