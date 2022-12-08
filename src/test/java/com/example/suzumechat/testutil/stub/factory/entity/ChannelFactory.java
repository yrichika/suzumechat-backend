package com.example.suzumechat.testutil.stub.factory.entity;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
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
public class ChannelFactory {

    @Autowired
    private TestRandom random;

    private String channelId = null;
    private String hostIdHashed = null;
    private String channelTokenId = null;
    private byte[] channelNameEnc = null;
    private String hostChannelTokenHashed = null;
    private byte[] hostChannelTokenEnc = null;
    private String joinChannelTokenHashed = null;
    private byte[] joinChannelTokenEnc = null;
    private String guestChannelTokenHashed = null;
    private byte[] guestChannelTokenEnc = null;
    private String publicKey = null;
    private byte[] secretKeyEnc = null;
    private Set<Guest> guests = Collections.emptySet();
    private Date updatedAt = null;
    private Date createdAt = null;

    public Channel make() {

        val channelId = TestHelper.getOrDefault(this.channelId,
            random.string.alphanumeric());
        val hostIdHashed = TestHelper.getOrDefault(this.hostIdHashed,
            random.string.alphanumeric());
        val channelTokenId = TestHelper.getOrDefault(this.channelTokenId,
            random.string.alphanumeric());
        val channelNameEnc = TestHelper.getOrDefault(this.channelNameEnc,
            random.string.alphanumeric().getBytes());
        val hostChannelTokenHashed = TestHelper.getOrDefault(
            this.hostChannelTokenHashed, random.string.alphanumeric());
        val hostChannelTokenEnc = TestHelper.getOrDefault(this.hostChannelTokenEnc,
            random.string.alphanumeric().getBytes());
        val joinChannelTokenHashed = TestHelper.getOrDefault(
            this.joinChannelTokenHashed, random.string.alphanumeric());
        val joinChannelTokenEnc = TestHelper.getOrDefault(this.joinChannelTokenEnc,
            random.string.alphanumeric().getBytes());
        val guestChannelTokenHashed = TestHelper.getOrDefault(
            this.guestChannelTokenHashed, random.string.alphanumeric());
        val guestChannelTokenEnc = TestHelper.getOrDefault(this.guestChannelTokenEnc,
            random.string.alphanumeric().getBytes());
        val publicKey = TestHelper.getOrDefault(this.publicKey, random.string.alphanumeric());
        // WARNING! null should be default. otherwise this nullable value can never
        // be null
        val secretKeyEnc = TestHelper.getOrDefault(this.secretKeyEnc, null);
        val guests = this.guests; // WARNING! not sync with channelId by default
        val updatedAt = TestHelper.getOrDefault(this.updatedAt, new Date());
        val createdAt = TestHelper.getOrDefault(this.createdAt, new Date());

        val channel = new Channel(null, // primary key should always be null for
                                        // testing, for
                                        // TestEntityManager `persist` method
            channelId, hostIdHashed, channelTokenId, channelNameEnc,
            hostChannelTokenHashed, hostChannelTokenEnc, joinChannelTokenHashed,
            joinChannelTokenEnc, guestChannelTokenHashed, guestChannelTokenEnc,
            publicKey, secretKeyEnc, guests, updatedAt, createdAt);

        reset();
        return channel;
    }

    private void reset() {
        channelId = null;
        hostIdHashed = null;
        channelTokenId = null;
        channelNameEnc = null;
        hostChannelTokenHashed = null;
        hostChannelTokenEnc = null;
        joinChannelTokenHashed = null;
        joinChannelTokenEnc = null;
        guestChannelTokenHashed = null;
        guestChannelTokenEnc = null;
        publicKey = null;
        secretKeyEnc = null;
        guests = Collections.emptySet();
        updatedAt = null;
        createdAt = null;
    }
}
