package com.example.suzumechat.service.channel.dto;

import java.util.List;

public record JoinRequestClosedNotification(String joinChannelToken, List<String> visitorIds) {

}
