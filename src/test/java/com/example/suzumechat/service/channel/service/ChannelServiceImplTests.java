package com.example.suzumechat.service.channel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByHostIdException;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByTokenException;
import com.example.suzumechat.service.channel.exception.HostChannelTokensMismatchException;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import com.example.suzumechat.utility.Random;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class ChannelServiceImplTests {
    @MockBean
    Hash hash;
    @MockBean
    Crypter crypter;
    @MockBean
    Random random;
    @MockBean
    ChannelRepository repository;

    @InjectMocks
    ChannelServiceImpl service;

    @Autowired
    ChannelFactory channelFactory;
    @Autowired
    GuestFactory guestFactory;

    @Autowired
    TestRandom testRandom;

    @Test
    public void create_should_create_channel_and_save_to_db() throws Exception {

        val channelName = testRandom.string.alphanumeric();
        val testRandomValue = testRandom.string.alphanumeric();
        val testRandomValueSecretKey = testRandom.string.alphanumeric();

        when(random.alphanumeric()).thenReturn(testRandomValue);
        when(random.alphanumeric(32)).thenReturn(testRandomValueSecretKey);

        CreatedChannel result = service.create(channelName);

        verify(repository, times(1)).save(any(Channel.class));

        assertThat(result.hostChannel().hostChannelToken())
            .isEqualTo(testRandomValue);
        assertThat(result.hostChannel().joinChannelToken())
            .isEqualTo(testRandomValue);
        assertThat(result.hostChannel().secretKey())
            .isEqualTo(testRandomValueSecretKey);
    }

    @Test
    public void getByHostChannelToken_should_return_channel_by_host_channel_token()
        throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.hostIdHashed(hostIdHashed)
            .hostChannelTokenHashed(hostChannelTokenHashed).make();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));

        val result = service.getByHostChannelToken(hostId, hostChannelToken);

        assertThat(result).isEqualTo(channel);
    }

    @Test
    public void getByHostChannelToken_should_throw_exception_if_channel_not_found()
        throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.empty());

        assertThrows(ChannelNotFoundByHostIdException.class, () -> {
            service.getByHostChannelToken(hostId, hostChannelToken);
        });
    }

    @Test
    public void getByHostChannelToken_should_throw_exception_if_hostChannelToken_does_not_match_with_token_in_db()
        throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        // NOT setting the hostChannelTokenHashed
        val channel = channelFactory.hostIdHashed(hostIdHashed).make();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));

        assertThrows(HostChannelTokensMismatchException.class, () -> {
            service.getByHostChannelToken(hostId, hostChannelToken);
        });
    }

    @Test
    public void getGuestChannelToken_should_return_gestChannelToken()
        throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val userSentHostChannelToken = testRandom.string.alphanumeric();
        val dbStoredHostChannelToken = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.hostChannelTokenHashed(dbStoredHostChannelToken)
            .make();
        val guestChannelToken = testRandom.string.alphanumeric();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));
        when(hash.digest(userSentHostChannelToken))
            .thenReturn(dbStoredHostChannelToken);
        when(crypter.decrypt(channel.getGuestChannelTokenEnc(),
            channel.getChannelId())).thenReturn(guestChannelToken);

        val result = service.getGuestChannelToken(hostId, userSentHostChannelToken);

        assertThat(result).isEqualTo(guestChannelToken);
    }

    @Test
    public void getGuestChannelToken_should_throw_if_retrieved_hostChannelToken_does_not_match_with_given_channel_token() {
        val hostId = testRandom.string.alphanumeric();
        val userSentHostChannelToken = testRandom.string.alphanumeric();

        val dbStoredHostChannelToken = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.hostChannelTokenHashed(dbStoredHostChannelToken)
            .make();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));
        val differentValueFromDbStoredHostChannelToken =
            testRandom.string.alphanumeric();
        when(hash.digest(userSentHostChannelToken))
            .thenReturn(differentValueFromDbStoredHostChannelToken);

        assertThrows(HostUnauthorizedException.class, () -> {
            service.getGuestChannelToken(hostId, userSentHostChannelToken);
        });
    }

    @Test
    public void getChannelByHostId_should_return_channel_if_found() {
        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));

        val result = service.getChannelByHostId(hostId);

        assertThat(result).isEqualTo(channel);
    }


    @Test
    public void getChannelByHostId_should_throw_exception_if_channel_not_found() {
        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.empty());

        assertThrows(HostUnauthorizedException.class, () -> {
            service.getChannelByHostId(hostId);
        });
    }

    @Test
    public void getByGuestChannelToken_should_return_guestChannel_by_guest_channel_token()
        throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val guestChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        when(hash.digest(guestChannelToken)).thenReturn(guestChannelTokenHashed);
        when(repository.findByGuestChannelTokenHashed(guestChannelTokenHashed))
            .thenReturn(Optional.of(channel));

        val result = service.getByGuestChannelToken(guestChannelToken);

        assertThat(result).isEqualTo(channel);
    }

    @Test
    public void getByGuestChannelToken_should_throw_exception_if_not_found()
        throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val guestChannelTokenHashed = testRandom.string.alphanumeric();

        when(hash.digest(guestChannelToken)).thenReturn(guestChannelTokenHashed);
        when(repository.findByGuestChannelTokenHashed(guestChannelTokenHashed))
            .thenReturn(Optional.empty());

        assertThrows(ChannelNotFoundByTokenException.class, () -> {
            service.getByGuestChannelToken(guestChannelToken);
        });
    }

    @Test
    public void getChannelNameByGuestChannelToken_should_return_channelName()
        throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val guestChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        val channelName = testRandom.string.alphanumeric();

        when(hash.digest(guestChannelToken)).thenReturn(guestChannelTokenHashed);
        when(repository.findByGuestChannelTokenHashed(guestChannelTokenHashed))
            .thenReturn(Optional.of(channel));
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        val result = service.getChannelNameByGuestChannelToken(guestChannelToken);

        assertThat(result).isEqualTo(channelName);
    }

    @Test
    public void getByJoinChannelToken_should_return_channel_by_joinChannelToken() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(repository.findByJoinChannelTokenHashed(joinChannelTokenHashed)).thenReturn(Optional.of(channel));
        val result = service.getByJoinChannelToken(joinChannelToken);
        assertThat(result).isEqualTo(channel);
    }


    @Test
    public void getByJoinChannelToken_should_throw_exception_if_channel_not_found()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(repository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.empty());

        assertThrows(ChannelNotFoundByTokenException.class, () -> {
            service.getByJoinChannelToken(joinChannelToken);
        });
    }

    @Test
    public void getHostChannelTokenByJoinChannelToken_should_return_hostChannelToken() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        val hostChannelToken = testRandom.string.alphanumeric();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(repository.findByJoinChannelTokenHashed(joinChannelTokenHashed)).thenReturn(Optional.of(channel));
        when(crypter.decrypt(channel.getHostChannelTokenEnc(), channel.getChannelId())).thenReturn(hostChannelToken);

        val result = service.getHostChannelTokenByJoinChannelToken(joinChannelToken);

        assertThat(result).isEqualTo(hostChannelToken);
    }

    @Test
    public void getByHostChannelToken_should_return_channel_by_hostChannelToken() throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();

        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostChannelTokenHashed(hostChannelTokenHashed)).thenReturn(Optional.of(channel));

        val result = service.getByHostChannelToken(hostChannelToken);
        assertThat(result).isEqualTo(channel);
    }

    @Test
    public void getByHostChannelToken_should_throw_exception_if_not_found() throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();

        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostChannelTokenHashed(hostChannelTokenHashed)).thenReturn(Optional.empty());

        assertThrows(ChannelNotFoundByTokenException.class, () -> {
            service.getByHostChannelToken(hostChannelToken);
        });
    }

    @Test
    public void getJoinChannelTokenByHostChannelToken_should_return_joinChannelToken() throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make();
        val joinChannelToken = testRandom.string.alphanumeric();

        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostChannelTokenHashed(hostChannelTokenHashed)).thenReturn(Optional.of(channel));
        when(crypter.decrypt(channel.getJoinChannelTokenEnc(), channel.getChannelId())).thenReturn(joinChannelToken);

        val result = service.getJoinChannelTokenByHostChannelToken(hostChannelToken);
        assertThat(result).isEqualTo(joinChannelToken);
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
    public void trashSecretKeyByHostChannelToken_should_delete_secret_key_specified_by_host_channel_token()
        throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();

        val channel = channelFactory.hostChannelTokenHashed(hostChannelTokenHashed)
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes()).make();

        when(hash.digest(hostId)).thenReturn(hostIdHashed);
        when(hash.digest(hostChannelToken)).thenReturn(hostChannelTokenHashed);
        when(repository.findByHostIdHashed(hostIdHashed))
            .thenReturn(Optional.of(channel));


        service.trashSecretKeyByHostChannelToken(hostId, hostChannelToken);

        // secretKeyEnc should be nulled
        val expectedChannel = channel.toBuilder().secretKeyEnc(null).build();
        verify(repository, times(1)).save(expectedChannel);
    }
}
