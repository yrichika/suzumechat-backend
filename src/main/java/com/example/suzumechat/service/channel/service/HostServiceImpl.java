package com.example.suzumechat.service.channel.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.utility.Crypter;
import lombok.val;


@Service
public class HostServiceImpl implements HostService {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private GuestService guestService;
    @Autowired
    private Crypter crypter;

    @Override
    public Optional<String> getGuestChannelToken(
        final String hostId,
        final String hostChannelToken) {
        try {
            val guestChannelToken =
                channelService.getGuestChannelToken(hostId, hostChannelToken);
            return Optional.of(guestChannelToken);
        } catch (Exception exception) {
            // TODO: log
            return Optional.empty();
        }
    }

    @Override
    public Optional<ApprovalResult> handleApproval(
        final String hostId,
        final String hostChannelToken,
        final String visitorId,
        final boolean isAuthenticated) {
        try {
            val guest = guestService.approveVisitor(visitorId, isAuthenticated);

            val channel =
                channelService.getByHostChannelToken(hostId, hostChannelToken);
            val joinChannelToken = crypter.decrypt(channel.getJoinChannelTokenEnc(),
                channel.getChannelId());

            // FIXME: closeしていれば、hostが返すこともできないので、
            // AuthenticationStatusにisClosedを持つことの意味がない
            if (channel.isClosed()) {
                val approvalResult = new ApprovalResult(joinChannelToken,
                    new AuthenticationStatus(true, null, "", "", "", ""));
                return Optional.of(approvalResult);
            }
            if (isAuthenticated == false) {
                val approvalResult = new ApprovalResult(joinChannelToken,
                    new AuthenticationStatus(false, isAuthenticated, "", "",
                        "", ""));
                return Optional.of(approvalResult);
            }

            val guestChannelToken = crypter.decrypt(
                channel.getGuestChannelTokenEnc(), channel.getChannelId());
            val guestId =
                crypter.decrypt(guest.getGuestIdEnc(), guest.getChannelId());
            val channelName = crypter.decrypt(channel.getChannelNameEnc(),
                channel.getChannelId());
            val secretKey = crypter.decrypt(channel.getSecretKeyEnc(),
                channel.getChannelId());
            val approvalResult = new ApprovalResult(joinChannelToken,
                new AuthenticationStatus(false, isAuthenticated, guestId,
                    guestChannelToken, channelName, secretKey));
            return Optional.of(approvalResult);
        } catch (Exception exception) {
            // TODO: log
            return Optional.empty();
        }
    }

    @Override
    public JoinRequestClosedNotification closeJoinRequest(
        final String hostId,
        final String hostChannelToken) throws Exception {

        channelService.trashSecretKeyByHostChannelToken(hostId, hostChannelToken);

        val channel = channelService.getByHostChannelToken(hostId, hostChannelToken);
        val visitorIds = guestService.getPendedVisitorIdsByChannel(channel);
        val joinChannelToken = crypter.decrypt(channel.getJoinChannelTokenEnc(), channel.getChannelId());

        return new JoinRequestClosedNotification(
            joinChannelToken,
            visitorIds);
    }
}
