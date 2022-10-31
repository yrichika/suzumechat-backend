package com.example.suzumechat.service.channel.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class HostUseCaseImplTests {
    @MockBean
    private ChannelService channelService;

    @InjectMocks
    private HostUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void test_endChannel_should_call_trash_() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();

        useCase.endChannel(hostId, hostChannelToken);

        verify(channelService, times(1)).trashSecretKeyByHostChannelToken(hostId,
                hostChannelToken);
    }
}
