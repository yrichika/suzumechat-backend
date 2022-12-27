package com.example.suzumechat.utility.messaging;

import com.example.suzumechat.utility.dto.message.ErrorMessage;

public interface MessageSender {
    public void toHost(String hostChannelToken, Object message);

    public void toVisitor(String joinChannelToken, String visitorId, Object message);

    public void toGuest(String guestChannelToken, Object message);

    public void broadcastToChat(String hostChannelToken, String guestChannelToken, String messageJson);

    /**
     * Send error messages only. This method is to make it clear that messages are
     * sending BACK and they are errors.
     */
    public void returningToHost(String hostChannelToken, ErrorMessage errorMessage);

    /**
     * Send error messages only. This method is to make it clear that messages are
     * sending BACK and they are errors.
     */
    public void returningToVisitor(String joinChannelToken, String visitorId, ErrorMessage errorMessage);

    /**
     * Send error messages only. This method is to make it clear that messages are
     * sending BACK and they are errors.
     */
    public void returningToGuest(String guestChannelToken, ErrorMessage errorMessage);
}
