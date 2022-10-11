package com.example.suzumechat.service.channel.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@Service
public class HostMessageHandlerServiceImpl implements HostMessageHandlerService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;
    @Autowired
    private Crypter crypter;

    @Override
    public String getGuestChannelToken(final String hostId,
            final String hostChannelToken) throws Exception {
        return channelService.getGuestChannelToken(hostId, hostChannelToken);
    }

    @Override
    public ApprovalResult handleApproval(final String hostId,
            final String hostChannelToken, final String visitorId,
            final boolean isAuthenticated) throws Exception {
        channelService.approveVisitor(visitorId, isAuthenticated);

        val channel = channelService.getByHostChannelToken(hostId, hostChannelToken);
        val joinChannelToken = crypter.decrypt(channel.getJoinChannelTokenEnc(),
                channel.getChannelId());

        if (channel.isClosed()) {
            return new ApprovalResult(joinChannelToken,
                    new AuthenticationStatus(true, null, ""));
        }
        if (isAuthenticated == false) {
            return new ApprovalResult(joinChannelToken,
                    new AuthenticationStatus(false, isAuthenticated, ""));
        }

        val guestChannelToken = crypter.decrypt(channel.getGuestChannelTokenEnc(),
                channel.getChannelId());
        return new ApprovalResult(joinChannelToken,
                new AuthenticationStatus(false, isAuthenticated, guestChannelToken));
    }
}
