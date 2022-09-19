package com.example.suzumechat.service.channel;

import java.util.List;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;

public interface ChannelService {
    public CreatedChannel create(final String channelName) throws Exception;

    public Channel getByHostChannelToken(final String hostId,
            final String hostChannelToken) throws Exception;

    public List<VisitorsStatus> getVisitorsStatus(final String channelId)
            throws Exception;

    public String getGuestChannelToken(final String hostId,
            final String userSentHostChannelToken) throws Exception;

    public List<Channel> getItemsOrderThan(final Integer hour);

    public void trashSecretKeyByHostChannelToken(final String hostChannelToken);
}
