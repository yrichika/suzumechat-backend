package com.example.suzumechat.service.guest.dto;

import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;

public record PendedJoinRequestResult(String hostChannelToken,
        VisitorsRequest visitorsRequest) {

}
