package com.example.suzumechat.testutil.stub.factory.form;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.form.CreatingChannel;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class CreatingChannelFactory {

    @Autowired
    private TestRandom random;

    private String channelName = null;
    private String publicKey = null;

    public CreatingChannel make() {
        val channelName = TestHelper.getOrDefault(this.channelName, random.string.alphanumeric());
        val publicKey = TestHelper.getOrDefault(this.publicKey, random.string.alphanumeric());
        val form = new CreatingChannel();
        form.setChannelName(channelName);
        form.setPublicKey(publicKey);
        reset();
        return form;
    }

    private void reset() {
        channelName = null;
        publicKey = null;
    }
}
