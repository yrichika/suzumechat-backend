package com.example.suzumechat.service.channel.application.messagehandler;

public interface HostMessageHandler {
    public void handle(String hostId, String hostChannelToken, String messageJson) throws Exception;
}
