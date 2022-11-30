package com.example.suzumechat.service.channel.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.utility.Crypter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;


@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class HostMessageHandlerServiceImplTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;
    @MockBean
    private Crypter crypter;

    @InjectMocks
    private HostMessageHandlerServiceImpl service;

    @Autowired
    private TestRandom testRandom;
    @Autowired
    private ChannelFactory channelFactory;
    @Autowired
    private GuestFactory guestFactory;


    @Test
    public void getGuestChannelToken_should_return_guestChannelToken()
        throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val expected = testRandom.string.alphanumeric();

        when(channelService.getGuestChannelToken(hostId, hostChannelToken))
            .thenReturn(expected);

        val result = service.getGuestChannelToken(hostId, hostChannelToken);
        assertThat(result.get()).isEqualTo(expected);
    }

    @Test
    public void getGuestChannelToken_should_return_empty_if_exception_thrown()
        throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();

        when(channelService.getGuestChannelToken(hostId, hostChannelToken))
            .thenThrow(new Exception());

        val result = service.getGuestChannelToken(hostId, hostChannelToken);
        assertThat(result.isEmpty()).isTrue();
    }


    @Test
    public void handleApproval_should_return_authenticated_result_if_approved()
        throws Exception {
        val secretKey = testRandom.string.alphanumeric().getBytes();
        val isAuthenticated = true;
        final HandleApprovalTestDto testDto =
            prepareForHandleApprovalTest(secretKey, isAuthenticated, false);

        final Optional<ApprovalResult> result =
            service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                testDto.visitorId, isAuthenticated);

        assertThat(result.get().joinChannelToken())
            .isEqualTo(testDto.joinChannelToken);
        assertThat(result.get().authenticationStatus().isClosed()).isFalse();
        assertThat(result.get().authenticationStatus().isAuthenticated()).isTrue();
        assertThat(result.get().authenticationStatus().guestId())
            .isEqualTo(testDto.guestId);
        assertThat(result.get().authenticationStatus().guestChannelToken())
            .isEqualTo(testDto.guestChannelToken);
        assertThat(result.get().authenticationStatus().channelName())
            .isEqualTo(testDto.channelName);
        assertThat(result.get().authenticationStatus().codename())
            .isEqualTo(testDto.codename);
        assertThat(result.get().authenticationStatus().secretKey())
            .isEqualTo(testDto.secretKey);
    }

    @Test
    public void handleApproval_should_return_closed_request_if_channel_closes()
        throws Exception {

        byte[] secretKeyEmptyMeansChannelClosed = null;
        final HandleApprovalTestDto testDto = prepareForHandleApprovalTest(
            secretKeyEmptyMeansChannelClosed, false, false);

        final Optional<ApprovalResult> result = service.handleApproval(
            testDto.hostId, testDto.hostChannelToken, testDto.visitorId, false);

        assertThat(result.get().joinChannelToken())
            .isEqualTo(testDto.joinChannelToken);
        assertThat(result.get().authenticationStatus().isClosed()).isTrue();
        assertThat(result.get().authenticationStatus().isAuthenticated()).isNull();
        assertThat(result.get().authenticationStatus().guestId()).isEqualTo("");
        assertThat(result.get().authenticationStatus().guestChannelToken())
            .isEqualTo("");
        assertThat(result.get().authenticationStatus().channelName()).isEqualTo("");
        assertThat(result.get().authenticationStatus().codename()).isEqualTo("");
        assertThat(result.get().authenticationStatus().secretKey()).isEqualTo("");

    }

    @Test
    public void handleApproval_should_return_isAuthenticated_false_and_guestChannelToken_empty_if_rejected()
        throws Exception {

        val secretKeyEnc = testRandom.string.alphanumeric().getBytes();
        val isAuthenticated = false;
        final HandleApprovalTestDto testDto =
            prepareForHandleApprovalTest(secretKeyEnc, isAuthenticated, false);

        final Optional<ApprovalResult> result =
            service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                testDto.visitorId, isAuthenticated);

        assertThat(result.get().joinChannelToken())
            .isEqualTo(testDto.joinChannelToken);
        assertThat(result.get().authenticationStatus().isClosed()).isFalse();
        assertThat(result.get().authenticationStatus().isAuthenticated()).isFalse();
        assertThat(result.get().authenticationStatus().guestId()).isEqualTo("");
        assertThat(result.get().authenticationStatus().guestChannelToken())
            .isEqualTo("");
        assertThat(result.get().authenticationStatus().guestChannelToken())
            .isEqualTo("");
        assertThat(result.get().authenticationStatus().channelName()).isEqualTo("");
        assertThat(result.get().authenticationStatus().codename()).isEqualTo("");
        assertThat(result.get().authenticationStatus().secretKey()).isEqualTo("");
    }

    @Test
    public void handleApproval_should_return_empty_if_exception_thrown()
        throws Exception {

        val isAuthenticated = false;
        final HandleApprovalTestDto testDto =
            prepareForHandleApprovalTest(null, isAuthenticated, true);

        final Optional<ApprovalResult> result =
            service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                testDto.visitorId, isAuthenticated);

        assertThat(result.isEmpty()).isTrue();
    }

    public HandleApprovalTestDto prepareForHandleApprovalTest(byte[] secretKeyEnc,
        boolean isAuthenticated, boolean throwsException) throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostIdHashed = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenHashed = testRandom.string.alphanumeric();
        val joinChannelToken = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        final Channel channel =
            channelFactory.secretKeyEnc(secretKeyEnc).hostIdHashed(hostIdHashed)
                .hostChannelTokenHashed(hostChannelTokenHashed).make();
        final Guest guest = guestFactory.channelId(channel.getChannelId()).make();
        val guestId = testRandom.string.alphanumeric();
        val channelName = testRandom.string.alphanumeric();
        val codename = testRandom.string.alphanumeric();
        val secretKey = testRandom.string.alphanumeric();

        if (throwsException) {
            when(guestService.approveVisitor(visitorId, isAuthenticated)).thenThrow(new Exception());
            when(channelService.getByHostChannelToken(hostId, hostChannelToken))
                .thenThrow(new Exception());
        } else {
            when(guestService.approveVisitor(visitorId, isAuthenticated)).thenReturn(guest);
            when(channelService.getByHostChannelToken(hostId, hostChannelToken))
                .thenReturn(channel);
        }

        when(crypter.decrypt(channel.getJoinChannelTokenEnc(),
            channel.getChannelId())).thenReturn(joinChannelToken);
        when(crypter.decrypt(channel.getGuestChannelTokenEnc(),
            channel.getChannelId())).thenReturn(guestChannelToken);
        when(crypter.decrypt(guest.getGuestIdEnc(), guest.getChannelId())).thenReturn(guestId);
        when(crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId())).thenReturn(channelName);
        when(crypter.decrypt(guest.getCodenameEnc(), guest.getChannelId())).thenReturn(codename);
        when(crypter.decrypt(channel.getSecretKeyEnc(), channel.getChannelId())).thenReturn(secretKey);

        return new HandleApprovalTestDto(hostId, visitorId, guestId, hostChannelToken,
            joinChannelToken, guestChannelToken, channelName, codename, secretKey);
    }


    @Accessors(fluent = true)
    @Getter
    @AllArgsConstructor
    class HandleApprovalTestDto {
        private String hostId;
        private String visitorId;
        private String guestId;
        private String hostChannelToken;
        private String joinChannelToken;
        private String guestChannelToken;
        private String channelName;
        private String codename;
        private String secretKey;
    }
}
