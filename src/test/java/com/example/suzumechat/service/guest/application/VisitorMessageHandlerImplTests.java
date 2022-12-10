package com.example.suzumechat.service.guest.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class VisitorMessageHandlerImplTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;

    @InjectMocks
    private VisitorMessageHandlerImpl messageHandler;


    @Autowired
    private ChannelFactory channelFactory;
    @Autowired
    private TestRandom testRandom;

    @Test
    public void createGuestAsVisitor_should_return_hostChannelToken_if_saved()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val visitorPublicKey = testRandom.string.alphanumeric();
        val whoIAmEnc = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory.make();

        when(channelService.getByJoinChannelToken(joinChannelToken)).thenReturn(channel);
        when(channelService.getHostChannelTokenByJoinChannelToken(joinChannelToken))
            .thenReturn(hostChannelToken);

        final Optional<String> result =
            messageHandler.createGuestAsVisitor(joinChannelToken, visitorId, visitorPublicKey, whoIAmEnc);

        assertThat(result.get()).isEqualTo(hostChannelToken);
    }

    @Test
    public void createGuestAsVisitor_should_return_empty_if_exception_thrown()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val visitorPublicKey = testRandom.string.alphanumeric();
        val whoIAmEnc = testRandom.string.alphanumeric();

        when(channelService.getHostChannelTokenByJoinChannelToken(joinChannelToken))
            .thenThrow(new Exception());

        final Optional<String> result =
            messageHandler.createGuestAsVisitor(joinChannelToken, visitorId, visitorPublicKey, whoIAmEnc);
        assertThat(result.isEmpty()).isTrue();
    }
}
