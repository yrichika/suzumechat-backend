package com.example.suzumechat.service.guest.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class VisitorUseCaseImplTests {

    @MockBean
    private Hash hash;
    @MockBean
    private Crypter crypter;
    @MockBean
    private ChannelService service;

    @InjectMocks
    private VisitorUseCaseImpl useCase;

    @Autowired
    private ChannelFactory channelFactory;

    @Autowired
    private TestRandom testRandom;

    @Test
    @DisplayName("ChannelStatus isAccepting should also be true")
    public void getChannelNameByJoinChannelToken_should_return_ChannelStatus()
        throws Exception {

        val joinChannelToken = testRandom.string.alphanumeric();
        val publicKey = testRandom.string.alphanumeric();
        val channel = channelFactory
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes())
            .publicKey(publicKey)
            .make();
        val channelName = testRandom.string.alphanumeric();

        when(service.getByJoinChannelToken(joinChannelToken)).thenReturn(channel);
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        final ChannelStatus channelStatus =
            useCase.getChannelStatusByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
        assertThat(channelStatus.hostPublicKey().get()).isEqualTo(publicKey);
        assertThat(channelStatus.isAccepting()).isTrue();
    }

    @Test
    public void getChannelNameByJoinChannelToken_should_return_ChannelStatus_isAccepting_false_if_secretKeyEnc_null()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory.make(); // secretKeyEnc is null
        val channelName = testRandom.string.alphanumeric();

        when(service.getByJoinChannelToken(joinChannelToken)).thenReturn(channel);
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        final ChannelStatus channelStatus =
            useCase.getChannelStatusByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
        assertThat(channelStatus.hostPublicKey()).isEqualTo(Optional.empty());
        assertThat(channelStatus.isAccepting()).isFalse(); // This is the difference
    }
}
