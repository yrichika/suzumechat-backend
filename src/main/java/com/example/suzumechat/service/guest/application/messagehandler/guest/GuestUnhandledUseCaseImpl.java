package com.example.suzumechat.service.guest.application.messagehandler.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.utility.dto.message.Unhandled;
import com.example.suzumechat.utility.messaging.MessageSender;

@Service
public class GuestUnhandledUseCaseImpl implements GuestUnhandledUseCase {
    @Autowired
    MessageSender messageSender;

    @Override
    public void handle(final String guestId, final String guestChannelToken, final String messageJson)
        throws Exception {
        messageSender.returningToGuest(guestChannelToken, new Unhandled(messageJson));
    }
}
