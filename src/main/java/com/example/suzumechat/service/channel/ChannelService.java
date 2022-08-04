package com.example.suzumechat.service.channel;

import java.util.List;

import com.example.suzumechat.service.channel.dto.CreatedChannel;

public interface ChannelService {
   public CreatedChannel create(String channelName) throws Exception;

   public List<Channel> getItemsOrderThan(Integer hour);

   public void trashSecretKeyByHostChannelToken(String hostChannelToken);
}
