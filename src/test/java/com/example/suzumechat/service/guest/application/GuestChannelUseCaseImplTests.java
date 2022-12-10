package com.example.suzumechat.service.guest.application;

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
import com.example.suzumechat.service.guest.dto.GuestChannel;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestChannelUseCaseImplTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;
    @MockBean
    private Crypter crypter;

    @InjectMocks
    private GuestChannelUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;
    @Autowired
    private ChannelFactory channelFactory;
    @Autowired
    private GuestFactory guestFactory;

    // DELETE:
    @Test
    public void getGuestChannelByGuestChannelToken_should_return_GuestChannel()
        throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val channelName = testRandom.string.alphanumeric();

        when(channelService.getChannelNameByGuestChannelToken(guestChannelToken))
            .thenReturn(channelName);

        final GuestChannel result =
            useCase.getGuestChannelByGuestChannelToken(guestChannelToken);
        assertThat(result.channelName()).isEqualTo(channelName);
    }

    @Test
    public void guestExistsInChannel_should_return_true_if_guest_exists()
        throws Exception {

        val guestChannelToken = testRandom.string.alphanumeric();
        val guestId = testRandom.string.alphanumeric();
        val sameChannelId = testRandom.string.alphanumeric();
        val channel = channelFactory.channelId(sameChannelId).make();
        val guest = guestFactory.channelId(sameChannelId).make();

        when(channelService.getByGuestChannelToken(guestChannelToken))
            .thenReturn(channel);
        when(guestService.getByGuestId(guestId)).thenReturn(guest);

        val result = useCase.guestExistsInChannel(guestId, guestChannelToken);

        assertThat(result).isTrue();
    }
}
