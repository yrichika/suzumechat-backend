package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class HostChannelFactory {

    @Autowired
    private TestRandom random;

    private String hostChannelToken = null;
    private String joinChannelToken = null;
    private String secretKey = null;

    public HostChannel make() {
        val hostChannelToken = TestHelper.getOrDefault(this.hostChannelToken, random.string.alphanumeric(5));
        val joinChannelToken = TestHelper.getOrDefault(this.joinChannelToken, random.string.alphanumeric(5));
        val secretKey = TestHelper.getOrDefault(this.secretKey, random.string.alphanumeric(5));

        val hostChannel = new HostChannel(hostChannelToken, joinChannelToken, secretKey);
        reset();
        return hostChannel;
    }

    public void reset() {
        hostChannelToken = null;
        joinChannelToken = null;
        secretKey = null;
    }
}
