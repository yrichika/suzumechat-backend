package com.example.suzumechat.testutil.stub.factory.dto;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class ChannelStatusFactory {

    @Autowired
    private TestRandom random;

    private String channelName = null;
    private String hostPublicKey = null;
    private Boolean isAccepting = null;

    public ChannelStatus make() {
        val channelName = TestHelper.getOrDefault(this.channelName, random.string.alphanumeric());
        // WARNING! default is empty
        final Optional<String> hostPublicKey =
            (this.hostPublicKey == null) ? Optional.empty() : Optional.of(random.string.alphanumeric());

        val isAccepting = TestHelper.getOrDefault(this.isAccepting, random.bool.nextBoolean());

        val channelStatus = new ChannelStatus(channelName, hostPublicKey, isAccepting);

        reset();
        return channelStatus;
    }

    private void reset() {
        channelName = null;
        hostPublicKey = null;
        isAccepting = null;
    }
}
