package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class CreatedChannelFactory {

    @Autowired
    private TestRandom random;
    @Autowired
    private HostChannelFactory hostChannelFactory;

    private String hostId = null;
    private HostChannel hostChannel = null;

    public CreatedChannel make() {
        val hostId = TestHelper.getOrDefault(this.hostId, random.string.alphanumeric(5));
        val randomHostChannel = hostChannelFactory.make();
        val hostChannel = TestHelper.getOrDefault(this.hostChannel, randomHostChannel);

        val createdChannel = new CreatedChannel(hostId, hostChannel);
        reset();
        return createdChannel;
    }

    private void reset() {
        hostId = null;
        hostChannel = null;
    }
}
