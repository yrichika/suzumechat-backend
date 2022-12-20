package com.example.suzumechat.service.channel;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    public Optional<Channel> findByChannelId(String channelId);

    public Optional<Channel> findByHostIdHashed(String hostIdHashed);

    public Optional<Channel> findByHostChannelTokenHashed(
        String hostChannelTokenHashed);

    public Optional<Channel> findByGuestChannelTokenHashed(
        String guestChannelTokenHashed);

    public Optional<Channel> findByJoinChannelTokenHashed(
        String joinChannelTokenHashed);

    @Query("select channel from Channel channel where channel.createdAt <= :createdAt")
    public List<Channel> findAllByCreatedAtBefore(
        @Param("createdAt") Date createdAt);

    @Modifying
    @Query("DELETE FROM Channel channel WHERE channel.channelId IN :channelIds")
    public int deleteByChannelIds(@Param("channelIds") List<String> channelIds);
}
