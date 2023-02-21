package com.example.suzumechat.service.guest.application.messagehandler.visitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VisitorUnhandledUseCaseImpl implements VisitorUnhandledUseCase {
    @Autowired
    private MessageSender messageSender;

    /*
     * In order to send visitor messages back, visitor id is necessary.
     * But messageJson may not contain visitor id.
     * That's why in this case it will not send message back to the visitor.
     * It just logs the message.
     */
    @Override
    public void handle(final String joinChannelToken, final String messageJson) throws Exception {
        log.warn("Visitor message unhandled: [" + messageJson + "]");
    }
}
