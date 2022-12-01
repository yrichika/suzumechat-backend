package com.example.suzumechat.service.guest.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.VisitorUseCase;
import com.example.suzumechat.service.guest.dto.ChannelStatus;

@RestController
public class VisitorController {

    @Autowired
    VisitorUseCase useCase;

    @Autowired
    private HttpSession session;

    // FIXME: "channelName" -> channelStatus か何か適した名前にすべて変える
    @GetMapping("/visitor/channelName/{joinChannelToken:.+}")
    public ChannelStatus channelName(
        @PathVariable("joinChannelToken") String joinChannelToken)
        throws Exception {
        final ChannelStatus channelStatus =
            useCase.getChannelNameByJoinChannelToken(joinChannelToken);
        return channelStatus;
    }
}
