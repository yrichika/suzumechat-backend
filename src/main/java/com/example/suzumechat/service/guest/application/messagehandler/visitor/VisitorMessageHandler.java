package com.example.suzumechat.service.guest.application.messagehandler.visitor;

public interface VisitorMessageHandler {
    public void handle(String joinChannelToken, String messageJson) throws Exception;
}
