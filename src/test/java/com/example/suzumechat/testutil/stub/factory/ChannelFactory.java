package com.example.suzumechat.testutil.stub.factory;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class ChannelFactory {

    @Autowired
    private TestRandom random;

    private String channelId = null;
    private String hostIdHashed = null;
    private String channelTokenId = null;
    private String channelNameEnc = null;
    private String hostChannelTokenHashed = null;
    private String loginChannelTokenHashed = null;
    private byte[] loginChannelTokenEnc = null;
    private String clientChannelTokenHashed = null;
    private byte[] clientChannelTokenEnc = null;
    private String secretKeyEnc = null;
    private Date updatedAt = null;
    private Date createdAt = null;

    public Channel make() {

        val channelId = TestHelper.getOrDefault(
            this.channelId, random.string.alphanumeric()
        );
        val hostIdHashed = TestHelper.getOrDefault(
            this.hostIdHashed, random.string.alphanumeric()
        );
        val channelTokenId = TestHelper.getOrDefault(
            this.channelTokenId, random.string.alphanumeric()
        );
        val channelNameEnc = TestHelper.getOrDefault(
            this.channelNameEnc, random.string.alphanumeric()
        );
        val hostChannelTokenHashed = TestHelper.getOrDefault(
            this.hostChannelTokenHashed, random.string.alphanumeric()
        );
        val loginChannelTokenHashed = TestHelper.getOrDefault(
            this.loginChannelTokenHashed, random.string.alphanumeric()
        );
        val loginChannelTokenEnc = TestHelper.getOrDefault(
            this.loginChannelTokenEnc, random.string.alphanumeric().getBytes()
        );
        val clientChannelTokenHashed = TestHelper.getOrDefault(
            this.clientChannelTokenHashed, random.string.alphanumeric()
        );
        val clientChannelTokenEnc = TestHelper.getOrDefault(
            this.clientChannelTokenEnc, random.string.alphanumeric().getBytes()
        );
        val secretKeyEnc = TestHelper.getOrDefault(
            this.secretKeyEnc, null
        );
        val updatedAt = TestHelper.getOrDefault(this.updatedAt, new Date());
        val createdAt = TestHelper.getOrDefault(this.createdAt, new Date());

        val channel = new Channel(
            null, // primary key should always be null for testing, for TestEntityManager `persist` method
            channelId,
            hostIdHashed,
            channelTokenId,
            channelNameEnc,
            hostChannelTokenHashed,
            loginChannelTokenHashed,
            loginChannelTokenEnc,
            clientChannelTokenHashed,
            clientChannelTokenEnc,
            secretKeyEnc,
            updatedAt,
            createdAt
        );

        reset();
        return channel;
    }

    private void reset() {
        channelId = null;
        hostIdHashed = null;
        channelTokenId = null;
        channelNameEnc = null;
        hostChannelTokenHashed = null;
        loginChannelTokenHashed = null;
        loginChannelTokenEnc = null;
        clientChannelTokenHashed = null;
        clientChannelTokenEnc = null;
        secretKeyEnc = null;
        updatedAt = null;
        createdAt = null;
    }
}
