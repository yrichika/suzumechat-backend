package com.example.suzumechat.service.guest.application;

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
import lombok.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

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
    private TestRandom testRandom;

    @Test
    public void createGuestAsVisitor_() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();

        when(channelService.getHostChannelTokenByJoinChannelToken(joinChannelToken))
                .thenReturn(hostChannelToken);

        final PendedJoinRequestResult result = service.createGuestAsVisitor(
                joinChannelToken, visitorId, codename, passphrase);

        assertThat(result.hostChannelToken()).isEqualTo(hostChannelToken);
        assertThat(result.visitorsRequest().visitorId()).isEqualTo(visitorId);
        assertThat(result.visitorsRequest().codename()).isEqualTo(codename);
        assertThat(result.visitorsRequest().passphrase()).isEqualTo(passphrase);
        assertThat(result.visitorsRequest().isAuthenticated()).isEmpty();

    }
}
