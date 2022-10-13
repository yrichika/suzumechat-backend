package com.example.suzumechat.service.guest.application;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestChannelServiceImplTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;

    @InjectMocks
    private GuestChannelServiceImpl service;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void getGuestChannelByGuestChannelToken_should_return_GuestChannel()
            throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val channelName = testRandom.string.alphanumeric();

        when(channelService.getChannelNameByGuestChannelToken(guestChannelToken))
                .thenReturn(channelName);

        final GuestChannel result =
                service.getGuestChannelByGuestChannelToken(guestChannelToken);
        assertThat(result.channelName()).isEqualTo(channelName);
    }
}
