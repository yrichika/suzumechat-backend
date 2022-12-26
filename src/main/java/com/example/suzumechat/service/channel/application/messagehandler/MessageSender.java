package com.example.suzumechat.service.channel.application.messagehandler;

import com.example.suzumechat.utility.dto.message.ErrorMessage;

public interface MessageSender {

    public void returningToHost(String hostChannelToken, ErrorMessage errorMessage);

    public void toVisitor(String joinChannelToken, String visitorId, String json);

    public void broadcastToChatChannel(String hostChannelToken, String guestChannelToken, String messageJson);

    public void toGuest(String guestChannelToken, String json);
}
