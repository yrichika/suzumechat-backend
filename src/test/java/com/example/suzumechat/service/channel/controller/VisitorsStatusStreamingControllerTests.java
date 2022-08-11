package com.example.suzumechat.service.channel.controller;

import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncListener;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.Mockito.*;

import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.VisitorsStatusFactory;

import lombok.*;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(VisitorsStatusStreamingController.class)
@MockitoSettings
public class VisitorsStatusStreamingControllerTests {
    
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ChannelService service;

    @Autowired
    TestRandom random;
    @Autowired
    VisitorsStatusFactory factory;

    final String urlPrefix = "/host/requestStatus/";
    private String hostChannelToken;
    private String url;

    @BeforeEach
    private void setUp() {
        hostChannelToken = random.string.alphanumeric();
        url = urlPrefix + hostChannelToken;
    }

    @Test
    public void fetch_should_return_SSE_of_visitorsStatus_list() throws Exception {

        val hostId = random.string.alphanumeric();
        val visitorStatus = factory.make();
        final List<VisitorsStatus> statuses = Arrays.asList(visitorStatus);
        when(service.getVisitorsStatus(hostId)).thenReturn(statuses);

        val request = get(url).sessionAttr("hostId", hostId);

        val preResult = mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();
        val asyncResultTimeLimited = limitMvcResultTimeForAsync(preResult, 4500L);
        val assertableStringResult = mockMvc
            .perform(asyncDispatch(asyncResultTimeLimited))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        val expectedPattern = resultStringPattern(visitorStatus);
        assertThat(assertableStringResult).containsPattern(expectedPattern);
    }


    private MvcResult limitMvcResultTimeForAsync(final MvcResult result, final long timeoutInMillisec) {
        
        MockAsyncContext asyncContext = (MockAsyncContext) result.getRequest().getAsyncContext();
        ScheduledExecutorService scheduledExec = Executors.newScheduledThreadPool(1);
        scheduledExec.schedule(() -> {
            for (AsyncListener listener: asyncContext.getListeners()) {
                try {
                    listener.onTimeout(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, timeoutInMillisec, TimeUnit.MILLISECONDS);
        result.getAsyncResult();

        return result;
    }

    private String resultStringPattern(final VisitorsStatus expected) {
        return "data:\\[\\{\\\"visitorId\\\":\\\""
            + expected.visitorId() + "\\\","
            + "\\\"codename\\\":" + "\\\"" + expected.codename() + "\\\","
            + "\\\"passphrase\\\":" + "\\\"" + expected.passphrase() + "\\\","
            + "\\\"isAuthenticated\\\":" + expected.isAuthenticated().toString()
            + ".*\\}\\]";
    }


    @Test
    public void fetch_should_return_401_if_hostId_session_value_does_not_exists() throws Exception {

        val request = get(url);

        mockMvc.perform(request)
            .andExpect(status().isUnauthorized());
    }
}
