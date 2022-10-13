package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.GuestChannel;

public interface GuestChannelService {

    public GuestChannel getGuestChannelByGuestChannelToken(
            final String guestChannelToken) throws Exception;
}
