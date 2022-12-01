package com.example.suzumechat.service.guest.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
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
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class VisitorUseCaseTests {

    @MockBean
    Hash hash;
    @MockBean
    Crypter crypter;
    @MockBean
    ChannelService service;

    @InjectMocks
    VisitorUseCaseImpl useCase;

    @Autowired
    GuestFactory guestFactory;
    @Autowired
    ChannelFactory channelFactory;

    @Autowired
    TestRandom testRandom;

    @Test
    @DisplayName("ChannelStatus isAccepting should also be true")
    public void getChannelNameByJoinChannelToken_should_return_ChannelStatus()
        throws Exception {

        val joinChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes()).make();
        val channelName = testRandom.string.alphanumeric();

        when(service.getByJoinChannelToken(joinChannelToken)).thenReturn(channel);
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        final ChannelStatus channelStatus =
            useCase.getChannelNameByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
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
            useCase.getChannelNameByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
        assertThat(channelStatus.isAccepting()).isFalse(); // This is the difference
    }
}
