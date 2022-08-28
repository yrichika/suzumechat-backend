package com.example.suzumechat.testutil.stub.factory.form;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.guest.form.JoinRequest;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class JoinRequestFactory {
    @Autowired
    private TestRandom random;

    private String codename = null;
    private String passphrase = null;

    public JoinRequest make() {
        val codename = TestHelper.getOrDefault(this.codename, random.string.alphanumeric());
        val passphrase = TestHelper.getOrDefault(this.passphrase, random.string.alphanumeric());

        val form = new JoinRequest();
        form.setCodename(codename);
        form.setPassphrase(passphrase);
        reset();
        return form;
    }

    private void reset() {
        codename = null;
        passphrase = null;
    }
}
