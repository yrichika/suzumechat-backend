package com.example.suzumechat.service.guest.application.messagehandler.guest;

public interface GuestMessageHandler {
    public void handle(String guestId, String guestChannelToken, String messageJson) throws Exception;
}
