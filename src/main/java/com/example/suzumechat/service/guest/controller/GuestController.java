package com.example.suzumechat.service.guest.controller;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.suzumechat.service.guest.application.GuestChannelUseCase;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.exception.GuestIdMissingInSessionException;
import com.example.suzumechat.service.guest.exception.GuestNotBelongingToChannelException;
import lombok.val;

@RestController
public class GuestController {
    @Autowired
    private GuestChannelUseCase useCase;
    @Autowired
    private HttpSession session;

    @GetMapping("guest/setSession/{guestChannelToken:.+}")
    public ResponseEntity<String> setSession(
        @PathVariable("guestChannelToken") @NotBlank @Size(
            max = 64) String guestChannelToken,
        @RequestParam("guestId") @NotBlank @Size(max = 64) final String guestId)
        throws Exception {

        if (!useCase.guestExistsInChannel(guestId, guestChannelToken)) {
            throw new GuestNotBelongingToChannelException();
        }
        session.setAttribute("guestId", guestId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("guest/endChat/{guestChannelToken:.+}")
    public ResponseEntity<String> endChat(
        @PathVariable("guestChannelToken") @NotBlank @Size(
            max = 64) String guestChannelToken)
        throws Exception {
        val guestId = (String) session.getAttribute("guestId");
        if (guestId == null) {
            throw new GuestIdMissingInSessionException();
        }

        if (!useCase.guestExistsInChannel(guestId, guestChannelToken)) {
            throw new GuestNotBelongingToChannelException();
        }
        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // DELETE:
    @GetMapping("/guest/guestChannel/{guestChannelToken:.+}")
    public GuestChannel guestChannel(
        @PathVariable("guestChannelToken") @NotBlank @Size(
            max = 64) String guestChannelToken)
        throws Exception {
        final GuestChannel guestChannel =
            useCase.getGuestChannelByGuestChannelToken(guestChannelToken);
        return guestChannel;
    }

}
