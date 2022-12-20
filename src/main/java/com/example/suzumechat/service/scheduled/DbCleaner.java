package com.example.suzumechat.service.scheduled;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.service.GuestService;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DbCleaner {

    @Autowired
    Environment env;

    @Autowired
    ChannelService channelService;

    @Autowired
    GuestService guestService;

    // TODO: move initialDelay and fixedDelay value to .properties
    @Transactional
    @Scheduled(initialDelay = 3600000, fixedDelay = 300000)
    public void clean() {
        log.info("Scheduled Task: cleaning expired channels.");

        val hours = Integer.parseInt(env.getProperty("db-clean.hour-old"));
        val channels = channelService.getItemsOrderThan(hours);
        List<String> channelIds = channels.stream().map(channel -> channel.getChannelId()).toList();
        val numDeleted = channelService.deleteByChannelIds(channelIds);
        guestService.deleteByChannelIds(channelIds);

        log.info("Scheduled Task: Cleaning completed. Deleted " + numDeleted + " channels");
    }
}
