package com.example.suzumechat.service.guest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.channel.dto.message.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.VisitorNotFoundException;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.exception.GuestNotFoundException;
import com.example.suzumechat.service.guest.exception.JoinChannelTokenInvalidException;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestServiceImplTests {
    @MockBean
    Hash hash;
    @MockBean
    Crypter crypter;
    @MockBean
    GuestRepository guestRepository;
    @MockBean
    ChannelRepository channelRepository;
    @InjectMocks
    GuestServiceImpl service;

    @Autowired
    GuestFactory guestFactory;
    @Autowired
    ChannelFactory channelFactory;

    @Autowired
    TestRandom testRandom;

    @Test
    @DisplayName("ChannelStatus isAccepting should also be true")
    public void getChannelNameByJoinChannelToken_should_return_ChannelStatus()
        throws Exception {

        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes()).make();
        val channelName = testRandom.string.alphanumeric();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.of(channel));
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        final ChannelStatus channelStatus =
            service.getChannelNameByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
        assertThat(channelStatus.isAccepting()).isTrue();
    }

    @Test
    public void getChannelNameByJoinChannelToken_should_return_ChannelStatus_isAccepting_false_if_secretKeyEnc_null()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val channel = channelFactory.make(); // secretKeyEnc is null
        val channelName = testRandom.string.alphanumeric();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.of(channel));
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId()))
            .thenReturn(channelName);

        final ChannelStatus channelStatus =
            service.getChannelNameByJoinChannelToken(joinChannelToken);

        assertThat(channelStatus.channelName()).isEqualTo(channelName);
        assertThat(channelStatus.isAccepting()).isFalse(); // This is the difference
    }

    @Test
    public void getByGuestId_should_return_guest_if_found() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestIdHashed = testRandom.string.alphanumeric();
        val guest = guestFactory.make();
        when(hash.digest(guestId)).thenReturn(guestIdHashed);
        when(guestRepository.findByGuestIdHashed(guestIdHashed))
            .thenReturn(Optional.of(guest));

        val result = service.getByGuestId(guestId);
        assertThat(result).isEqualTo(guest);
    }

    @Test
    public void getByGuestId_should_throw_exception_if_not_found() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestIdHashed = testRandom.string.alphanumeric();

        when(hash.digest(guestId)).thenReturn(guestIdHashed);
        when(guestRepository.findByGuestIdHashed(guestIdHashed))
            .thenReturn(Optional.empty());
        assertThrows(GuestNotFoundException.class, () -> {
            service.getByGuestId(guestId);
        });
    }

    @Test
    public void createGuestAsVisitor_should_save_guest_as_visitor_and_return_visitor_id()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();
        val channel = channelFactory
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes()).make();
        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.of(channel));

        Optional<String> result = service.createGuestAsVisitor(joinChannelToken,
            visitorId, codename, passphrase);

        verify(guestRepository, times(1)).save(any(Guest.class));
        assertThat(result.get()).isNotEmpty();
    }

    @Test
    public void createGuestAsVisitor_should_return_null_if_channel_not_found()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();
        val channel = channelFactory.make(); // secretKeyEnc is null
        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.of(channel));

        Optional<String> result = service.createGuestAsVisitor(joinChannelToken,
            visitorId, codename, passphrase);

        verify(guestRepository, times(0)).save(any(Guest.class));
        assertThat(result.isEmpty()).isTrue();
    }


    @Test
    public void updateStatus_should_set_isAuthenticated_to_specified_value()
        throws Exception {
        val visitorId = testRandom.string.alphanumeric();
        val isAuthenticated = testRandom.bool.nextBoolean();
        val visitorIdHashed = testRandom.string.alphanumeric();
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);

        service.updateStatus(visitorId, isAuthenticated);

        verify(guestRepository, times(1)).updateIsAuthenticatedByVisitorIdHashed(
            visitorIdHashed, isAuthenticated);
    }


    @Test
    public void getChannelByJoinChannelToken_should_throw_exception_if_channel_not_found()
        throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val joinChannelTokenHashed = testRandom.string.alphanumeric();

        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);
        when(channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed))
            .thenReturn(Optional.empty());

        assertThrows(JoinChannelTokenInvalidException.class, () -> {
            service.getChannelByJoinChannelToken(joinChannelToken);
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
    public void approveVisitor_should_set_guest_id_and_isAuthenticated_if_isAuthenticated_true()
        throws Exception {

        val isAuthenticated = true; // DIFFERENCE!
        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();

        Guest guest = spy(new Guest());
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(guestRepository.findByVisitorIdHashed(visitorIdHashed))
            .thenReturn(Optional.of(guest));

        val guestResult = service.approveVisitor(visitorId, isAuthenticated);

        // the parameter is set to null because unable to mock guestIdHashed.
        // the parameter should be like `anyString()`
        // verify(guest, times(1)).setGuestIdHashed(anyString());
        verify(guest, times(1)).setGuestIdHashed(null);
        // same here. want the parameter to be `any(byte[].class)`
        // verify(guest, times(1)).setGuestIdEnc(any(byte[].class));
        verify(guest, times(1)).setGuestIdEnc(null);
        verify(guest, times(1)).setIsAuthenticated(isAuthenticated);
        verify(guestRepository, times(1)).save(any(Guest.class));

        assertThat(guestResult.getIsAuthenticated()).isTrue();
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

        val guestResult = service.approveVisitor(visitorId, isAuthenticated);

        verify(guest, never()).setGuestIdHashed(null);
        verify(guest, never()).setGuestIdEnc(null);
        verify(guest, times(1)).setIsAuthenticated(isAuthenticated);
        verify(guestRepository, times(1)).save(any(Guest.class));

        assertThat(guestResult.getIsAuthenticated()).isFalse();
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
}
