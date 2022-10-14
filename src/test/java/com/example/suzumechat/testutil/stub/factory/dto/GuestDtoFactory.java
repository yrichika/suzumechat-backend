package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.GuestDto;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class GuestDtoFactory {

    @Autowired
    private TestRandom random;

    private String codename = null;
    private String secretKey = null;

    public GuestDto make() {
        val codename = TestHelper.getOrDefault(this.codename,
                random.string.alphanumeric(5));
        val secretKey = TestHelper.getOrDefault(this.secretKey,
                random.string.alphanumeric(5));

        val guestDto = new GuestDto(codename, secretKey);
        reset();
        return guestDto;
    }

    public void reset() {
        codename = null;
        secretKey = null;
    }
}
