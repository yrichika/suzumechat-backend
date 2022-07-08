package com.example.suzumechat.service.channel;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    public Channel findByChannelId(String channelId);

    public Channel findByHostIdHashed(String hostIdHashed);

    public Channel findByHostChannelTokenHashed(String hostChannelTokenHashed);

    public Channel findByGuestChannelTokenHashed(String guestChannelTokenHashed);

    public Channel findByLoginChannelTokenHashed(String loginChannelTokenHashed);

    @Query("select channel from Channel channel where channel.createdAt <= :createdAt")
    public List<Channel> findAllByCreatedAtBefore(@Param("createdAt") Date createdAt);

    public void deleteByIdIn(List<Integer> ids);

    public void deleteByChannelIdIn(List<String> channelIds);


}
