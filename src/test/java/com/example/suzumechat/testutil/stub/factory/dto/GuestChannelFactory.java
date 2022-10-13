package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class GuestChannelFactory {
    @Autowired
    private TestRandom testRandom;

    private String channelName = null;


    public GuestChannel make() {
        val channelName = TestHelper.getOrDefault(this.channelName,
                testRandom.string.alphanumeric(5));

        val hostChannel = new GuestChannel(channelName);
        reset();
        return hostChannel;
    }

    public void reset() {
        channelName = null;
    }
}
