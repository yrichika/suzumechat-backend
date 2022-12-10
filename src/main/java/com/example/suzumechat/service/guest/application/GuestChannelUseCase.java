package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.GuestChannel;

public interface GuestChannelUseCase {

    // DELETE:
    public GuestChannel getGuestChannelByGuestChannelToken(
        final String guestChannelToken) throws Exception;


    public boolean guestExistsInChannel(final String guestId,
        final String guestChannelToken) throws Exception;

}
