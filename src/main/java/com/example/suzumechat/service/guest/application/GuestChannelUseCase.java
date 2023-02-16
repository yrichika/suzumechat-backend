package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.GuestChannel;

public interface GuestChannelUseCase {

    // DELETE:
    public GuestChannel getGuestChannelByGuestChannelToken(
        String guestChannelToken) throws Exception;


    public boolean guestExistsInChannel(String guestId,
        String guestChannelToken) throws Exception;

}
