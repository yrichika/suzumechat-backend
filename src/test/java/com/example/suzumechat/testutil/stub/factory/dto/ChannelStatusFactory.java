package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class ChannelStatusFactory {

    @Autowired
    private TestRandom random;

    private String channelName = null;
    private Boolean isAccepting = null;

    public ChannelStatus make() {
        String channelName = TestHelper.getOrDefault(this.channelName, random.string.alphanumeric());
        boolean isAccepting = TestHelper.getOrDefault(this.isAccepting, random.bool.nextBoolean());

        val channelStatus = new ChannelStatus(channelName, isAccepting);
        reset();
        return channelStatus;
    }

    private void reset() {
        channelName = null;
        isAccepting = null;
    }
}
