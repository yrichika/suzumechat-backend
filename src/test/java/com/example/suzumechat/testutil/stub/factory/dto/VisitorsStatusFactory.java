package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class VisitorsStatusFactory {

    @Autowired
    TestRandom random;

    String visitorId = null;
    String codename = null;
    String passphrase = null;
    Boolean isAuthenticated = null;

    public VisitorsStatus make() {
        val visitorId = TestHelper.getOrDefault(this.visitorId, random.string.alphanumeric());
        val codename = TestHelper.getOrDefault(this.codename, random.string.alphanumeric());
        val passphrase = TestHelper.getOrDefault(this.passphrase, random.string.alphanumeric());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated, random.bool.nextBoolean());
        val visitorsStatus = new VisitorsStatus(visitorId, codename, passphrase, isAuthenticated);
        reset();
        return visitorsStatus;

    }

    private void reset() {
        visitorId = null;
        codename = null;
        passphrase = null;
        isAuthenticated = null;
    }
}
