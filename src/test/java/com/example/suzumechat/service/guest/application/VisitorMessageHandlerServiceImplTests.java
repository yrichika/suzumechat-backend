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
import com.example.suzumechat.service.guest.dto.PendedJoinRequestResult;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class VisitorMessageHandlerServiceImplTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;

    @InjectMocks
    private VisitorMessageHandlerServiceImpl service;


    @Autowired
    private ChannelFactory channelFactory;
    @Autowired
    private TestRandom testRandom;

    @Test
    public void createGuestAsVisitor_should_return_pended_result_if_saved()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory.make();

        when(channelService.getByJoinChannelToken(joinChannelToken)).thenReturn(channel);
        when(channelService.getHostChannelTokenByJoinChannelToken(joinChannelToken))
            .thenReturn(hostChannelToken);

        final Optional<PendedJoinRequestResult> result =
            service.createGuestAsVisitor(joinChannelToken, visitorId, codename,
                passphrase);

        assertThat(result.get().hostChannelToken()).isEqualTo(hostChannelToken);
        assertThat(result.get().managedJoinRequest().visitorId())
            .isEqualTo(visitorId);
        assertThat(result.get().managedJoinRequest().codename()).isEqualTo(codename);
        assertThat(result.get().managedJoinRequest().passphrase())
            .isEqualTo(passphrase);
        assertThat(result.get().managedJoinRequest().isAuthenticated()).isEmpty();
    }

    @Test
    public void createGuestAsVisitor_should_return_empty_if_exception_thrown()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();

        when(channelService.getHostChannelTokenByJoinChannelToken(joinChannelToken))
            .thenThrow(new Exception());

        final Optional<PendedJoinRequestResult> result =
            service.createGuestAsVisitor(joinChannelToken, visitorId, codename,
                passphrase);
        assertThat(result.isEmpty()).isTrue();
    }
}
