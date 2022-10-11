package com.example.suzumechat.service.channel.dto;

import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;

public record ApprovalResult(String joinChannelToken,
        AuthenticationStatus authenticationStatus) {

}
