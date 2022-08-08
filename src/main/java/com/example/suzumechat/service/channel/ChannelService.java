package com.example.suzumechat.service.channel;

import java.util.List;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;

public interface ChannelService {
   public CreatedChannel create(String channelName) throws Exception;

   public List<VisitorsStatus> getVisitorsStatus(String hostId) throws Exception;

   public List<Channel> getItemsOrderThan(Integer hour);

   public void trashSecretKeyByHostChannelToken(String hostChannelToken);
}
