package com.example.suzumechat.service.channel.application.messagehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.utility.dto.message.Unhandled;
import com.example.suzumechat.utility.messaging.MessageSender;

@Service
public class UnhandledUseCaseImpl implements UnhandledUseCase {
    @Autowired
    MessageSender messageSender;

    public void handle(String hostId, String hostChannelToken, String messageJson) throws Exception {
        messageSender.returningToHost(hostChannelToken, new Unhandled(messageJson));
    }
}
