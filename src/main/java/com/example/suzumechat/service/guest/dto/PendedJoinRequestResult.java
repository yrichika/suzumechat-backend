package com.example.suzumechat.service.guest.dto;

import com.example.suzumechat.service.guest.dto.message.ManagedJoinRequest;

public record PendedJoinRequestResult(String hostChannelToken,
        ManagedJoinRequest managedJoinRequest) {

}
