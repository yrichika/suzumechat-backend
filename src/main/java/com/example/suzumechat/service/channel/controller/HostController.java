package com.example.suzumechat.service.channel.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.channel.application.HostUseCase;
import com.example.suzumechat.service.channel.exception.HostIdMissingInSessionException;
import lombok.val;

@RestController
public class HostController {

    @Autowired
    private HostUseCase useCase;

    @Autowired
    private HttpSession session;

    @PostMapping("/host/endChannel/{hostChannelToken:.+}")
    public ResponseEntity<String> endChannel(
        @PathVariable("hostChannelToken") final String hostChannelToken)
        throws Exception {

        val hostId = (String) session.getAttribute("hostId");
        if (hostId == null) {
            throw new HostIdMissingInSessionException();
        }

        useCase.endChannel(hostId, hostChannelToken);

        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // TODO: close visitor requests: delete secret key
}
