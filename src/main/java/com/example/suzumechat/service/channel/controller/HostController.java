package com.example.suzumechat.service.channel.controller;

import javax.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.form.VisitorsAuthStatus;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.utility.form.ValidationOrder;
import lombok.val;

@RestController
public class HostController {

    @Autowired
    private ChannelService service;

    @Autowired
    private HttpSession session;

    @PostMapping("/host/approveRequest/{hostChannelToken:.+}")
    public ResponseEntity<String> approveRequest(
            @PathVariable("hostChannelToken") final String hostChannelToken,
            @Validated(ValidationOrder.class) @RequestBody final VisitorsAuthStatus form)
            throws Exception {

        service.approveVisitor(form.getVisitorId(), form.getIsAuthenticated());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/host/endChannel/{hostChannelToken:.+}")
    public ResponseEntity<String> endChannel(
            @PathVariable("hostChannelToken") final String hostChannelToken)
            throws Exception {

        val hostId = (String) session.getAttribute("hostId");

        service.trashSecretKeyByHostChannelToken(hostId, hostChannelToken);

        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // TODO: close visitor requests: delete secret key
}
