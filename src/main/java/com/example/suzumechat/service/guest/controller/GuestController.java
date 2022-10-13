package com.example.suzumechat.service.guest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.GuestChannelService;
import com.example.suzumechat.service.guest.dto.GuestChannel;

@RestController
public class GuestController {
    @Autowired
    private GuestChannelService service;


    @GetMapping("/guest/guestChannel/{guestChannelToken:.+}")
    public GuestChannel guestChannel(
            @PathVariable("guestChannelToken") String guestChannelToken)
            throws Exception {
        final GuestChannel guestChannel =
                service.getGuestChannelByGuestChannelToken(guestChannelToken);
        return guestChannel;
    }
    // TODO: 2. guestIdの問い合わせがあれば、codename, secretKeyを返す
    // このタイミングで、guestIdをHttpSessionに保存
}
