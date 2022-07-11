package com.example.suzumechat.service.channel;

import static org.mockito.ArgumentMatchers.any;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.ChannelFactory;
import com.example.suzumechat.utility.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class ChannelServiceImplTests {
    @MockBean Hash hash;
    @MockBean Crypter crypter;
    @MockBean Random random;
    @MockBean ChannelRepository repository;

    @InjectMocks
    ChannelServiceImpl service;

    @Autowired
    ChannelFactory factory;

    @Autowired
    TestRandom testRandom;

    @Test
    public void create_should_create_channel_and_save_to_db() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val channelName = testRandom.string.alphanumeric();
        val testRandomValue = testRandom.string.alphanumeric();
        val testRandomValueSecretKey = testRandom.string.alphanumeric();
        
        when(hash.digest(hostId)).thenReturn(testRandomValue);
        when(random.alphanumeric()).thenReturn(testRandomValue);
        when(random.alphanumeric(32)).thenReturn(testRandomValueSecretKey);

        CreatedChannel result = service.create(hostId, channelName);

        verify(repository, times(1)).save(any(Channel.class));
        assertThat(result.getHostChannelToken()).isEqualTo(testRandomValue);
        assertThat(result.getLoginChannelToken()).isEqualTo(testRandomValue);
        assertThat(result.getSecretKey()).isEqualTo(testRandomValueSecretKey);
    }

    @Test
    public void getItemsOrderThan_should_return_list_of_channel_before_specified_time() {
        
        val hours = testRandom.integer.nextInt(10);
        // val hoursAgo = LocalDateTime.now().minusHours(hours);
        // val hoursAgoTimestamp = Timestamp.valueOf(hoursAgo).toInstant();
        // val date = Date.from(hoursAgoTimestamp);

        // TODO: not a complete test. It's hard to test exact time.
        List<Channel> result = service.getItemsOrderThan(hours);

        verify(repository, times(1)).findAllByCreatedAtBefore(any(Date.class));
    }

    @Test
    public void trashSecretKeyByHostChannelToken_should_delete_secret_key_specified_by_host_channel_token() {

        val hostChannelToken = testRandom.string.alphanumeric();
        val digestValue = testRandom.string.alphanumeric();
        val channel = factory.secretKeyEnc(
            testRandom.string.alphanumeric().getBytes()
        ).make();
        // Clone of the channel with different. It's not necessary because `channel.secretKeyEnc`
        // will be nulled in the tested method. But it's just to clarify what's saved in the method.
        val savedChannel = channel.toBuilder().secretKeyEnc(null).build();

        when(hash.digest(hostChannelToken)).thenReturn(digestValue);
        when(repository.findByHostChannelTokenHashed(digestValue)).thenReturn(channel);

        service.trashSecretKeyByHostChannelToken(hostChannelToken);

        verify(repository, times(1)).findByHostChannelTokenHashed(digestValue);
        verify(repository, times(1)).save(savedChannel);
    }
}
