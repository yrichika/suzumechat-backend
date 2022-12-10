package com.example.suzumechat.testutil.stub.factory.entity;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class GuestFactory {

    @Autowired
    private TestRandom random;

    @Autowired
    private ChannelFactory channelFactory;

    private String guestIdHashed = null;
    private byte[] guestIdEnc = null;
    private Boolean isAuthenticated = null;
    private String visitorIdHashed = null;
    private byte[] visitorIdEnc = null;
    private String channelId = null;
    private Channel channel = null;
    private Date updatedAt = null;
    private Date createdAt = null;

    public Guest make() {
        val guestIdHashed =
            TestHelper.getOrDefault(this.guestIdHashed, random.string.alphanumeric());
        val guestIdEnc =
            TestHelper.getOrDefault(this.guestIdEnc, random.string.alphanumeric().getBytes());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated, null);
        val visitorIdHashed =
            TestHelper.getOrDefault(this.visitorIdHashed, random.string.alphanumeric());
        val visitorIdEnc =
            TestHelper.getOrDefault(this.visitorIdEnc, random.string.alphanumeric().getBytes());

        String channelId = TestHelper.getOrDefault(this.channelId, random.string.alphanumeric());
        Channel channel = channelFactory.channelId(channelId).make();
        if (this.channel != null) {
            channelId = this.channel.getChannelId();
            channel = this.channel;
        }

        val updatedAt = TestHelper.getOrDefault(this.updatedAt, new Date());
        val createdAt = TestHelper.getOrDefault(this.createdAt, new Date());


        val guest = new Guest(null, guestIdHashed, guestIdEnc, isAuthenticated, visitorIdHashed, visitorIdEnc,
            channelId, channel, updatedAt,
            createdAt);
        reset();
        return guest;
    }

    private void reset() {
        guestIdHashed = null;
        guestIdEnc = null;
        isAuthenticated = null;
        visitorIdHashed = null;
        visitorIdEnc = null;
        channelId = null;
        channel = null;
        updatedAt = null;
        createdAt = null;
    }

}
