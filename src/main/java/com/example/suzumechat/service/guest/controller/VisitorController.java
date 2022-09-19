package com.example.suzumechat.service.guest.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.GuestService;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.ReceptionStatus;
import com.example.suzumechat.service.guest.form.JoinRequest;
import com.example.suzumechat.utility.form.ValidationOrder;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class VisitorController {

    @Autowired
    GuestService service;

    @Autowired
    private HttpSession session;

    @GetMapping("/visitor/channelName/{joinChannelToken:.+}")
    public ChannelStatus channelName(
            @PathVariable("joinChannelToken") String joinChannelToken)
            throws Exception {
        final ChannelStatus channelStatus =
                service.getChannelNameByJoinChannelToken(joinChannelToken);
        return channelStatus;
    }

    @PostMapping("/visitor/joinRequest/{joinChannelToken:.+}")
    public ReceptionStatus joinRequest(
            @PathVariable("joinChannelToken") String joinChannelToken,
            @Validated(ValidationOrder.class) @RequestBody final JoinRequest form)
            throws Exception {
        final Optional<String> visitorId = service.createGuestAsVisitor(
                joinChannelToken, form.getCodename(), form.getPassphrase());

        if (visitorId.isEmpty()) {
            return new ReceptionStatus(false);
        }

        session.setAttribute("visitorId", visitorId.get());

        // FIXME: change response code to 201(CREATED)
        return new ReceptionStatus(true);
    }

}
