package com.example.suzumechat.service.channel.application;

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
import com.example.suzumechat.testutil.stub.factory.dto.CreatedChannelFactory;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class ChannelUseCaseImplTests {
    @MockBean
    private ChannelService service;

    @InjectMocks
    private ChannelUseCaseImpl useCase;

    @Autowired
    private CreatedChannelFactory createdChannelFactory;
    @Autowired
    private TestRandom testRandom;

    @Test
    public void test_create_should_return_CreatedChannel() throws Exception {

        val channelName = testRandom.string.alphanumeric();
        val publicKey = testRandom.string.alphanumeric();
        val createdChannel = createdChannelFactory.make();

        when(service.create(channelName, publicKey)).thenReturn(createdChannel);

        val result = useCase.create(channelName, publicKey);

        assertThat(result).isEqualTo(createdChannel);
    }
}
