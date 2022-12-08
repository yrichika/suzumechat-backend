package com.example.suzumechat.service.guest.dto;

import java.util.Optional;

public record ChannelStatus(String channelName, Optional<String> hostPublicKey, boolean isAccepting) {
}
