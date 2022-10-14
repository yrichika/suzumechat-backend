package com.example.suzumechat.service.guest.controller;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.GuestChannelService;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.dto.GuestDto;

@RestController
public class GuestController {
    @Autowired
    private GuestChannelService service;
    @Autowired
    private HttpSession session;


    @GetMapping("/guest/guestChannel/{guestChannelToken:.+}")
    public GuestChannel guestChannel(
            @PathVariable("guestChannelToken") @NotBlank @Size(
                    max = 64) String guestChannelToken)
            throws Exception {
        final GuestChannel guestChannel =
                service.getGuestChannelByGuestChannelToken(guestChannelToken);
        return guestChannel;
    }

    @GetMapping("/guest/guestDto/{guestChannelToken:.+}")
    public GuestDto guestDto(
            @PathVariable("guestChannelToken") @NotBlank @Size(
                    max = 64) String guestChannelToken,
            @RequestParam("guestId") @NotBlank @Size(max = 64) final String guestId)
            throws Exception {
        final GuestDto guestDto =
                service.getGuestDtoByGuestId(guestId, guestChannelToken);
        session.setAttribute("guestId", guestId);
        return guestDto;
    }

}
