package com.example.suzumechat.service.channel;

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
import com.example.suzumechat.utility.form.ValidationOrder;

import lombok.*;

@RestController
public class ChannelController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ChannelService service;

    @PostMapping("/createChannel")
    public HostChannel create(
        @Validated(ValidationOrder.class)
        @RequestBody
        final CreatingChannel form,
        final HttpSession session
    ) throws Exception {

        final CreatedChannel channel = service.create(form.getChannelName());

        session.setAttribute("hostId", channel.getHostId());
        session.setAttribute("secretKeyHost", channel.getHostChannel().getSecretKey());
        return channel.getHostChannel();
    }

}
