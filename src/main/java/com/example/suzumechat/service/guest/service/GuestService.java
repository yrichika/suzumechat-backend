package com.example.suzumechat.service.guest.service;

import java.util.List;
import java.util.Optional;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.guest.Guest;

public interface GuestService {

    public Guest getByGuestId(String guestId) throws Exception;

    public Optional<String> createGuestAsVisitor(String joinChannelToken,
        String visitorId, Channel channel) throws Exception;

    public void updateStatus(String visitorId, Boolean isAuthenticated);

    public Guest approveVisitor(String visitorId, boolean isAuthenticated)
        throws Exception;

    public List<String> getPendedVisitorIdsByChannel(Channel channel) throws Exception;

    public int deleteByChannelIds(List<String> channelIds);
}
