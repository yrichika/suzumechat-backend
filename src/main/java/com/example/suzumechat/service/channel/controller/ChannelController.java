package com.example.suzumechat.service.channel.controller;

import javax.servlet.http.HttpSession;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.service.channel.form.CreatingChannel;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.utility.form.ValidationOrder;

import lombok.*;

@RestController
public class ChannelController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ChannelService service;
    @Autowired
    private HttpSession session;

    @PostMapping("/createChannel")
    public HostChannel create(
            @Validated(ValidationOrder.class) @RequestBody final CreatingChannel form)
            throws Exception {

        // TODO: If a hostId already exists in session, redirect to the host page
        // Prevent creating another channel

        final CreatedChannel channel = service.create(form.getChannelName());

        session.setAttribute("hostId", channel.hostId());
        session.setAttribute("secretKeyHost", channel.hostChannel().secretKey());
        // FIXME: change response code to 201(CREATED)
        return channel.hostChannel();
    }


}
