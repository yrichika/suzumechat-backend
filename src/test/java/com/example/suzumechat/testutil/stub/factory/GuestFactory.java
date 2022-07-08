package com.example.suzumechat.testutil.stub.factory;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class GuestFactory {
    
    @Autowired
    private TestRandom random;

    private String guestIdHashed = null;
    private byte[] guestIdEnc = null;
    private byte[] codenameEnc = null;
    private byte[] passphraseEnc = null;
    private Boolean isAuthenticated = null;
    private String visitorIdHashed = null;
    private byte[] visitorIdEnc = null;
    private String channelId = null;
    private Date updatedAt = null;
    private Date createdAt = null;

    public Guest make() {
        val guestIdHashed = TestHelper.getOrDefault(this.guestIdHashed, random.string.alphanumeric());
        val guestIdEnc = TestHelper.getOrDefault(this.guestIdEnc, random.string.alphanumeric().getBytes());
        val codenameEnc = TestHelper.getOrDefault(this.codenameEnc, random.string.alphanumeric().getBytes());
        val passphraseEnc = TestHelper.getOrDefault(this.passphraseEnc, random.string.alphanumeric().getBytes());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated, true);
        val visitorIdHashed = TestHelper.getOrDefault(this.visitorIdHashed, random.string.alphanumeric());
        val visitorIdEnc = TestHelper.getOrDefault(this.visitorIdEnc, random.string.alphanumeric().getBytes());
        val channelId = TestHelper.getOrDefault(this.channelId, random.string.alphanumeric());
        val updatedAt = TestHelper.getOrDefault(this.updatedAt, new Date());
        val createdAt = TestHelper.getOrDefault(this.createdAt, new Date());
        
        val guest = new Guest(
            null,
            guestIdHashed,
            guestIdEnc,
            codenameEnc,
            passphraseEnc,
            isAuthenticated,
            visitorIdHashed,
            visitorIdEnc,
            channelId,
            updatedAt,
            createdAt
        );
        reset();
        return guest;
    }

    private void reset() {
        guestIdHashed = null;
        guestIdEnc = null;
        codenameEnc = null;
        passphraseEnc = null;
        isAuthenticated = null;
        visitorIdHashed = null;
        visitorIdEnc = null;
        channelId = null;
        updatedAt = null;
        createdAt = null;
    }

}