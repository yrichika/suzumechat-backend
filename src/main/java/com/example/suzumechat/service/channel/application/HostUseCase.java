package com.example.suzumechat.service.channel.application;

public interface HostUseCase {
    public void closeJoinRequest(String hostId, String hostChannelToken) throws Exception;
}
