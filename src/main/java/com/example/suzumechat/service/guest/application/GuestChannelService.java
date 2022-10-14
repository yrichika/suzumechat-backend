package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.dto.GuestDto;

public interface GuestChannelService {

    public GuestChannel getGuestChannelByGuestChannelToken(
            final String guestChannelToken) throws Exception;

    public GuestDto getGuestDtoByGuestId(final String guestId,
            final String guestChannelToken) throws Exception;
}
