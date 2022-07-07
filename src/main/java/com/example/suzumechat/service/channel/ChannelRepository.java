package com.example.suzumechat.service.channel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    public Channel findByChannelId(String channelId);

    public Channel findByHostIdHashed(String hostIdHashed);

    public Channel findByHostChannelTokenHashed(String hostChannelTokenHashed);

    public Channel findByClientChannelTokenHashed(String clientChannelTokenHashed);

    public Channel findByLoginChannelTokenHashed(String loginChannelTokenHashed);

    public void deleteByIdIn(List<Integer> ids);

    public void deleteByChannelIdIn(List<String> channelIds); // TODO: これが動くかテスト


}
