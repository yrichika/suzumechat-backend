package com.example.suzumechat.service.channel.application;


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
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.utility.Crypter;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.experimental.Accessors;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


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


    @Test
    public void getGuestChannelToken_should_return_guestChannelToken()
            throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val expected = testRandom.string.alphanumeric();

        when(channelService.getGuestChannelToken(hostId, hostChannelToken))
                .thenReturn(expected);

        val result = service.getGuestChannelToken(hostId, hostChannelToken);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void handleApproval_should_return_authenticated_result_if_approved()
            throws Exception {
        val secretKey = testRandom.string.alphanumeric().getBytes();
        final HandleApprovalTestDto testDto =
                prepareForHandleApprovalTest(secretKey, true);

        final ApprovalResult result =
                service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                        testDto.visitorId, testDto.isAuthenticated);

        assertThat(result.joinChannelToken()).isEqualTo(testDto.joinChannelToken);
        assertThat(result.authenticationStatus().isClosed()).isFalse();
        assertThat(result.authenticationStatus().isAuthenticated()).isTrue();
        assertThat(result.authenticationStatus().guestChannelToken())
                .isEqualTo(testDto.guestChannelToken);
    }

    public void handleApproval_should_return_closed_request_if_channel_closes()
            throws Exception {

        final HandleApprovalTestDto testDto =
                prepareForHandleApprovalTest(null, null);

        final ApprovalResult result =
                service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                        testDto.visitorId, testDto.isAuthenticated);

        assertThat(result.joinChannelToken()).isEqualTo(testDto.joinChannelToken);
        assertThat(result.authenticationStatus().isClosed()).isTrue();
        assertThat(result.authenticationStatus().isAuthenticated()).isNull();
        assertThat(result.authenticationStatus().guestChannelToken()).isEqualTo("");
    }

    public void handleApproval_should_return_isAuthenticated_false_and_guestChannelToken_empty_if_rejected()
            throws Exception {

        val secretKeyEnc = testRandom.string.alphanumeric().getBytes();
        final HandleApprovalTestDto testDto =
                prepareForHandleApprovalTest(secretKeyEnc, false);

        final ApprovalResult result =
                service.handleApproval(testDto.hostId, testDto.hostChannelToken,
                        testDto.visitorId, testDto.isAuthenticated);

        assertThat(result.joinChannelToken()).isEqualTo(testDto.joinChannelToken);
        assertThat(result.authenticationStatus().isClosed()).isFalse();
        assertThat(result.authenticationStatus().isAuthenticated()).isFalse();
        assertThat(result.authenticationStatus().guestChannelToken()).isEqualTo("");
    }

    public HandleApprovalTestDto prepareForHandleApprovalTest(byte[] secretKeyEnc,
            Boolean isAuthenticated) throws Exception {
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

        when(channelService.getByHostChannelToken(hostId, hostChannelToken))
                .thenReturn(channel);
        when(crypter.decrypt(channel.getJoinChannelTokenEnc(),
                channel.getChannelId())).thenReturn(joinChannelToken);
        when(crypter.decrypt(channel.getGuestChannelTokenEnc(),
                channel.getChannelId())).thenReturn(guestChannelToken);

        return new HandleApprovalTestDto(hostId, visitorId, hostChannelToken,
                joinChannelToken, guestChannelToken, isAuthenticated);
    }

    @Accessors(fluent = true)
    @Getter
    @AllArgsConstructor
    class HandleApprovalTestDto {
        private String hostId;
        private String visitorId;
        private String hostChannelToken;
        private String joinChannelToken;
        private String guestChannelToken;
        private boolean isAuthenticated;
    }
}
