package com.example.suzumechat.service.guest.application;

import com.example.suzumechat.service.guest.dto.ChannelStatus;

public interface VisitorUseCase {
    public ChannelStatus getChannelStatusByJoinChannelToken(String joinChannelToken)
        throws Exception;
}
