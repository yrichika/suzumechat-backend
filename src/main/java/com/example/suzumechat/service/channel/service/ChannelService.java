package com.example.suzumechat.service.channel.service;

import java.util.List;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.dto.CreatedChannel;

public interface ChannelService {
    public CreatedChannel create(final String channelName) throws Exception;

    // FIXME: change method name to another appropriate one
    public Channel getByHostChannelToken(final String hostId,
        final String hostChannelToken) throws Exception;

    public String getGuestChannelToken(final String hostId,
        final String userSentHostChannelToken) throws Exception;

    public Channel getByGuestChannelToken(final String guestChannelToken)
        throws Exception;

    public String getChannelNameByGuestChannelToken(final String guestChannelToken)
        throws Exception;

    public Channel getByJoinChannelToken(final String joinChannelToken)
        throws Exception;

    public String getHostChannelTokenByJoinChannelToken(
        final String joinChannelToken) throws Exception;

    public Channel getByHostChannelToken(final String hostChannelToken)
        throws Exception;

    public String getJoinChannelTokenByHostChannelToken(
        final String hostChannelToken) throws Exception;

    public List<Channel> getItemsOrderThan(final Integer hour);

    public void trashSecretKeyByHostChannelToken(final String hostId,
        final String hostChannelToken) throws Exception;
}
