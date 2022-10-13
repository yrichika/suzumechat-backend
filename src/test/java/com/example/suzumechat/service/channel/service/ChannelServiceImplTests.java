package com.example.suzumechat.service.channel.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
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
import com.example.suzumechat.service.channel.dto.message.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByHostIdException;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByTokenException;
import com.example.suzumechat.service.channel.exception.HostChannelTokensMismatchException;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;
import com.example.suzumechat.service.channel.exception.VisitorNotFoundException;
import com.example.suzumechat.service.channel.service.ChannelServiceImpl;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @MockBean
    GuestRepository guestRepository;

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
    public void getVisitorsStatus_should_return_all_visitors_status_belongs_to_the_channel()
            throws Exception {
        val channel = channelFactory.make();
        final List<Guest> guests = new ArrayList<>();
        int howMany = testRandom.integer.between(1, 5);
        for (int i = 0; i < howMany; i++) {
            val guest = guestFactory.make();
            guests.add(guest);
        }

        final List<String> valuesToAssertSimply =
                setUpGetStatusVisitorMock(channel, guests);

        final List<VisitorsStatus> result =
                service.getVisitorsStatus(channel.getChannelId());

        for (int i = 0; i < guests.size(); i++) {
            assertThat(result.get(i).visitorId())
                    .isEqualTo(valuesToAssertSimply.get(i));
            assertThat(result.get(i).visitorId())
                    .isEqualTo(valuesToAssertSimply.get(i));
            assertThat(result.get(i).visitorId())
                    .isEqualTo(valuesToAssertSimply.get(i));
            assertThat(result.get(i).isAuthenticated())
                    .isEqualTo(guests.get(i).getIsAuthenticated());
        }
    }


    private List<String> setUpGetStatusVisitorMock(final Channel channel,
            final List<Guest> guests) throws Exception {

        when(guestRepository.findAllByChannelIdOrderByIdDesc(channel.getChannelId()))
                .thenReturn(guests);

        final List<String> valuesToAssertSimply = new ArrayList<>();
        for (Guest guest : guests) {
            val valueToAssertSimply = testRandom.string.alphanumeric();
            when(crypter.decrypt(guest.getVisitorIdEnc(), channel.getChannelId()))
                    .thenReturn(valueToAssertSimply);
            when(crypter.decrypt(guest.getCodenameEnc(), channel.getChannelId()))
                    .thenReturn(valueToAssertSimply);
            when(crypter.decrypt(guest.getPassphraseEnc(), channel.getChannelId()))
                    .thenReturn(valueToAssertSimply);
            valuesToAssertSimply.add(valueToAssertSimply);
        }
        return valuesToAssertSimply;
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
    public void approveVisitor_should_set_guest_id_and_isAuthenticated_if_isAuthenticated_true()
            throws Exception {

        val isAuthenticated = true; // DIFFERENCE!
        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();

        Guest guest = spy(new Guest());
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(guestRepository.findByVisitorIdHashed(visitorIdHashed))
                .thenReturn(Optional.of(guest));

        val guestId = service.approveVisitor(visitorId, isAuthenticated);

        // the parameter is set to null because unable to mock guestIdHashed.
        // the parameter should be like `anyString()`
        // verify(guest, times(1)).setGuestIdHashed(anyString());
        verify(guest, times(1)).setGuestIdHashed(null);
        // same here. want the parameter to be `any(byte[].class)`
        // verify(guest, times(1)).setGuestIdEnc(any(byte[].class));
        verify(guest, times(1)).setGuestIdEnc(null);
        verify(guest, times(1)).setIsAuthenticated(isAuthenticated);
        verify(guestRepository, times(1)).save(any(Guest.class));
        assertThat(guestId).isNotEmpty();
    }


    @Test
    public void approveVisitor_should_only_set_isAuthenticated_if_isAuthenticated_false()
            throws Exception {

        val isAuthenticated = false; // DIFFERENCE!
        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();

        Guest guest = spy(new Guest());
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(guestRepository.findByVisitorIdHashed(visitorIdHashed))
                .thenReturn(Optional.of(guest));

        val result = service.approveVisitor(visitorId, isAuthenticated);

        verify(guest, never()).setGuestIdHashed(null);
        verify(guest, never()).setGuestIdEnc(null);
        verify(guest, times(1)).setIsAuthenticated(isAuthenticated);
        verify(guestRepository, times(1)).save(any(Guest.class));
        assertThat(result).isEqualTo("");
    }


    @Test
    public void approveVisitor_should_throw_exception_if_guest_not_found()
            throws Exception {

        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();
        val isAuthenticated = testRandom.bool.nextBoolean();

        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(guestRepository.findByVisitorIdHashed(visitorIdHashed))
                .thenReturn(Optional.empty());

        assertThrows(VisitorNotFoundException.class, () -> {
            service.approveVisitor(visitorId, isAuthenticated);
        });
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
