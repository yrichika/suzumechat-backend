package com.example.suzumechat.service.guest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.GuestService;
import com.example.suzumechat.service.guest.dto.ChannelStatus;

import lombok.val;

@RestController
public class VisitorController {
    
    @Autowired
    GuestService service;

    @GetMapping("/visitor/channelName/{joinChannelToken:.+}")
    public ChannelStatus channelName(@PathVariable("joinChannelToken") String joinChannelToken) throws Exception {
        final ChannelStatus channelStatus = service.getChannelNameByJoinChannelToken(joinChannelToken);
        return channelStatus;
    }
}
