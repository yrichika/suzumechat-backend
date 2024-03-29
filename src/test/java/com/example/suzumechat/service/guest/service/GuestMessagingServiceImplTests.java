package com.example.suzumechat.service.guest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestMessagingServiceImplTests {

    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;
    @MockBean
    private Crypter crypter;
    @InjectMocks
    private GuestMessagingServiceImpl messagingService;

    @Autowired
    private ChannelFactory channelFactory;
    @Autowired
    private GuestFactory guestFactory;
    @Autowired
    private TestRandom testRandom;

    @Test
    public void getHostChannelToken_should_get_optional_of_hostChannelToken()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        val guest = guestFactory.channelId(channel.getChannelId()).make();
        val hostChannelToken = testRandom.string.alphanumeric();

        when(channelService.getByGuestChannelToken(guestChannelToken))
            .thenReturn(channel);
        when(guestService.getByGuestId(guestId)).thenReturn(guest);
        when(crypter.decrypt(channel.getHostChannelTokenEnc(),
            channel.getChannelId())).thenReturn(hostChannelToken);

        val result = messagingService.getHostChannelToken(guestId, guestChannelToken);

        assertThat(result.get()).isEqualTo(hostChannelToken);
    }

    @Test
    public void getHostChannelToken_should_return_optional_empty_if_exception_thrown()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();

        when(channelService.getByGuestChannelToken(guestChannelToken))
            .thenThrow(Exception.class);

        val result = messagingService.getHostChannelToken(guestId, guestChannelToken);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void getHostChannelToken_should_return_optional_empty_if_guest_does_not_belong_to_the_channel()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        // guest has different channelId
        val guest = guestFactory.make();

        when(channelService.getByGuestChannelToken(guestChannelToken))
            .thenReturn(channel);
        when(guestService.getByGuestId(guestId)).thenReturn(guest);

        val result = messagingService.getHostChannelToken(guestId, guestChannelToken);

        assertThat(result.isEmpty()).isTrue();
    }
}
