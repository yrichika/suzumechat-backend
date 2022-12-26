package com.example.suzumechat.service.channel.application.messagehandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.dto.JoinRequestClosedNotification;
import com.example.suzumechat.service.channel.service.HostService;
import com.example.suzumechat.service.guest.dto.message.JoinRequestClosed;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class CloseJoinRequestUseCaseImplTests {
    @MockBean
    private HostService hostService;
    @MockBean
    private ObjectMapper mapper;
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private CloseJoinRequestUseCaseImpl useCase;

    @Autowired
    TestRandom testRandom;

    @Test
    public void handle_should_close_join_request_and_send_JoinRequestClosed_message_to_all_visitors() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val joinChannelToken = testRandom.string.alphanumeric();
        val closedMessage = new JoinRequestClosed(true);
        val json = "{}";
        final List<String> visitorIds = new ArrayList<String>();
        val howMany = testRandom.integer.nextInt(5);
        for (int i = 0; i < howMany; i++) {
            visitorIds.add(testRandom.string.alphanumeric());
        }

        when(hostService.closeJoinRequest(hostId, hostChannelToken))
            .thenReturn(new JoinRequestClosedNotification(joinChannelToken, visitorIds));
        when(mapper.writeValueAsString(closedMessage)).thenReturn(json);

        useCase.handle(hostId, hostChannelToken, json);

        visitorIds.forEach(visitorId -> {
            verify(messageSender, times(1)).toVisitor(joinChannelToken, visitorId, json);
        });
    }
}
