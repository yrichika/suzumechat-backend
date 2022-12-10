package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.guest.Guest;

public interface GuestService {

    public Guest getByGuestId(final String guestId) throws Exception;

    public Optional<String> createGuestAsVisitor(String joinChannelToken,
        String visitorId, Channel channel) throws Exception;

    public void updateStatus(String visitorId, Boolean isAuthenticated);

    public Guest approveVisitor(String visitorId, boolean isAuthenticated)
        throws Exception;
}
