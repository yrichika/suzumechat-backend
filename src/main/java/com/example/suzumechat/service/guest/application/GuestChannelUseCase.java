package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.dto.GuestDto;

public interface GuestChannelUseCase {

    // DELETE:
    public GuestChannel getGuestChannelByGuestChannelToken(
        final String guestChannelToken) throws Exception;

    // DELETE:
    public GuestDto getGuestDtoByGuestId(final String guestId,
        final String guestChannelToken) throws Exception;

    public boolean guestExistsInChannel(final String guestId,
        final String guestChannelToken) throws Exception;

}
