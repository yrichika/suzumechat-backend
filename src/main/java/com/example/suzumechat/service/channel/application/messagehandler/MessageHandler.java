package com.example.suzumechat.service.channel.application.messagehandler;

public interface MessageHandler {
    public void handle(String hostId, String hostChannelToken, String messageJson) throws Exception;
}
