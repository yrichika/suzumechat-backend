package com.example.suzumechat.service.channel.service;

import java.util.List;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.dto.CreatedChannel;

public interface ChannelService {
    public CreatedChannel create(String channelName, String publicKey) throws Exception;

    // FIXME: change method name to another appropriate one
    public Channel getByHostChannelToken(String hostId,
        String hostChannelToken) throws Exception;

    public String getGuestChannelToken(String hostId,
        String userSentHostChannelToken) throws Exception;

    public Channel getByGuestChannelToken(String guestChannelToken)
        throws Exception;

    public String getChannelNameByGuestChannelToken(String guestChannelToken)
        throws Exception;

    public Channel getByJoinChannelToken(String joinChannelToken)
        throws Exception;

    public String getHostChannelTokenByJoinChannelToken(
        String joinChannelToken) throws Exception;

    public List<Channel> getItemsOrderThan(Integer hour);

    public void trashSecretKeyByHostChannelToken(String hostId,
        String hostChannelToken) throws Exception;

    public int deleteByChannelIds(List<String> channelIds);
}
