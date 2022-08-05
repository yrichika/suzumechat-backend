package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class HostChannelFactory {

    @Autowired
    private TestRandom random;
    @Autowired
    private ChannelFactory channelFactory;

    private String hostChannelToken = null;
    private String loginChannelToken = null;
    private String secretKey = null;

    public HostChannel make() {
        val hostChannelToken = TestHelper.getOrDefault(this.hostChannelToken, random.string.alphanumeric(5));
        val loginChannelToken = TestHelper.getOrDefault(this.loginChannelToken, random.string.alphanumeric(5));
        val secretKey = TestHelper.getOrDefault(this.secretKey, random.string.alphanumeric(5));

        val hostChannel = new HostChannel(hostChannelToken, loginChannelToken, secretKey);
        reset();
        return hostChannel;
    }

    public void reset() {
        hostChannelToken = null;
        loginChannelToken = null;
        secretKey = null;
    }
}
