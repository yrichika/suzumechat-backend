package com.example.suzumechat.service.guest.service;

import org.hibernate.jdbc.Expectations;
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
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;
import com.example.suzumechat.service.guest.exception.JoinChannelTokenInvalidException;
import com.example.suzumechat.service.guest.exception.VisitorInvalidException;
import com.example.suzumechat.service.guest.service.GuestServiceImpl;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;

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

}
