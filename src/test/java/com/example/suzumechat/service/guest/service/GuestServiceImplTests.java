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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.exception.VisitorNotFoundException;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.exception.GuestNotFoundException;
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
    private Hash hash;
    @MockBean
    private Crypter crypter;
    @MockBean
    private GuestRepository repository;

    @InjectMocks
    private GuestServiceImpl service;

    @Autowired
    private GuestFactory guestFactory;
    @Autowired
    private ChannelFactory channelFactory;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void getByGuestId_should_return_guest_if_found() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestIdHashed = testRandom.string.alphanumeric();
        val guest = guestFactory.make();
        when(hash.digest(guestId)).thenReturn(guestIdHashed);
        when(repository.findByGuestIdHashed(guestIdHashed))
            .thenReturn(Optional.of(guest));

        val result = service.getByGuestId(guestId);
        assertThat(result).isEqualTo(guest);
    }

    @Test
    public void getByGuestId_should_throw_exception_if_not_found() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestIdHashed = testRandom.string.alphanumeric();

        when(hash.digest(guestId)).thenReturn(guestIdHashed);
        when(repository.findByGuestIdHashed(guestIdHashed))
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
        val channel = channelFactory
            .secretKeyEnc(testRandom.string.alphanumeric().getBytes()).make();
        when(hash.digest(joinChannelToken)).thenReturn(joinChannelTokenHashed);

        Optional<String> result = service.createGuestAsVisitor(joinChannelToken,
            visitorId, channel);

        verify(repository, times(1)).save(any(Guest.class));
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

        Optional<String> result = service.createGuestAsVisitor(joinChannelToken,
            visitorId, channel);

        verify(repository, times(0)).save(any(Guest.class));
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

        verify(repository, times(1)).updateIsAuthenticatedByVisitorIdHashed(
            visitorIdHashed, isAuthenticated);
    }

    @Test
    public void approveVisitor_should_set_guest_id_and_isAuthenticated_if_isAuthenticated_true()
        throws Exception {

        val isAuthenticated = true; // DIFFERENCE!
        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();

        Guest guest = spy(new Guest());
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(repository.findByVisitorIdHashed(visitorIdHashed))
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
        verify(repository, times(1)).save(any(Guest.class));

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
        when(repository.findByVisitorIdHashed(visitorIdHashed))
            .thenReturn(Optional.of(guest));

        val guestResult = service.approveVisitor(visitorId, isAuthenticated);

        verify(guest, never()).setGuestIdHashed(null);
        verify(guest, never()).setGuestIdEnc(null);
        verify(guest, times(1)).setIsAuthenticated(isAuthenticated);
        verify(repository, times(1)).save(any(Guest.class));

        assertThat(guestResult.getIsAuthenticated()).isFalse();
    }


    @Test
    public void approveVisitor_should_throw_exception_if_guest_not_found()
        throws Exception {

        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();
        val isAuthenticated = testRandom.bool.nextBoolean();

        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(repository.findByVisitorIdHashed(visitorIdHashed))
            .thenReturn(Optional.empty());

        assertThrows(VisitorNotFoundException.class, () -> {
            service.approveVisitor(visitorId, isAuthenticated);
        });
    }

    @Test
    public void getVisitorIdsByChannel_should_return_pended_visitors_ids() throws Exception {
        val channel = channelFactory.make();
        val howManyVisitors = testRandom.integer.between(1, 5);
        final Map<String, Guest> visitorMap = new HashMap<String, Guest>();
        for (Integer i = 0; i < howManyVisitors; i++) {
            visitorMap.put(i.toString(), guestFactory.make());
        }
        final List<Guest> visitorList = new ArrayList<Guest>(visitorMap.values());

        when(repository.getPendedVisitorsByChannelId(channel.getChannelId()))
            .thenReturn(visitorList);
        for (Map.Entry<String, Guest> visitorEntry : visitorMap.entrySet()) {
            when(crypter.decrypt(
                visitorEntry.getValue().getVisitorIdEnc(),
                channel.getChannelId()))
                    .thenReturn(visitorEntry.getKey());
        }

        val result = service.getPendedVisitorIdsByChannel(channel);

        visitorMap.forEach((visitorId, visitor) -> {
            assertThat(result.contains(visitorId)).isTrue();
        });
    }

    @Test
    public void deleteByChannelIds_should_call_repository_deleteByChannelIds() {
        val channelIds = Arrays.asList(testRandom.string.alphanumeric());

        service.deleteByChannelIds(channelIds);

        verify(repository, times(1)).deleteByChannelIds(channelIds);
    }
}
