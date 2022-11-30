package com.example.suzumechat.service.guest.service;

import java.util.List;
import java.util.Optional;
import com.example.suzumechat.service.channel.dto.message.VisitorsStatus;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.dto.ChannelStatus;

public interface GuestService {

    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken)
        throws Exception;

    public Guest getByGuestId(final String guestId) throws Exception;

    // originally ClientLoginRequests.create()
    public Optional<String> createGuestAsVisitor(String joinChannelToken,
        String visitorId, String codename, String passphrase) throws Exception;

    // originally ClientLoginRequests.updateStatus
    public void updateStatus(String visitorId, Boolean isAuthenticated);

    // DELETE: not used
    public List<VisitorsStatus> getVisitorsStatus(final String channelId)
        throws Exception;

    public Guest approveVisitor(String visitorId, boolean isAuthenticated)
        throws Exception;
}
