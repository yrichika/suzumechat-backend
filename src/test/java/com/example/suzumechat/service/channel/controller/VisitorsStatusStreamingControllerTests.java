package com.example.suzumechat.service.channel.controller;

import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.Mockito.*;

import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.VisitorsStatusFactory;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;

import lombok.*;
import reactor.test.StepVerifier;

// TODO: WebFluxTestの方が簡単にかけるのか試してみたい
// https://spring.pleiades.io/spring-boot/docs/2.0.2.RELEASE/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-webflux-tests
@Import({TestConfig.class, SecurityConfig.class})
@WebFluxTest(controllers = VisitorsStatusStreamingController.class,
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@MockitoSettings
public class VisitorsStatusStreamingControllerTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ChannelService service;
    @MockBean
    private HttpSession httpSession;

    @Autowired
    private TestRandom random;
    @Autowired
    private VisitorsStatusFactory visitorStatusFactory;

    @Autowired
    private ChannelFactory channelFactory;

    private static final ParameterizedTypeReference<ServerSentEvent<List<VisitorsStatus>>> typeRef =
            new ParameterizedTypeReference<>() {};


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
        val channel = channelFactory.make();
        val visitorStatus = visitorStatusFactory.make();
        final List<VisitorsStatus> statuses = Arrays.asList(visitorStatus);

        when(httpSession.getAttribute("hostId")).thenReturn(hostId);
        when(service.getByHostChannelToken(hostId, hostChannelToken))
                .thenReturn(channel);
        when(service.getVisitorsStatus(channel.getChannelId())).thenReturn(statuses);

        final FluxExchangeResult<ServerSentEvent<List<VisitorsStatus>>> result =
                webClient.get().uri(url).accept(MediaType.ALL).exchange()
                        .expectStatus().isOk().returnResult(typeRef);
        StepVerifier.create(result.getResponseBody()).expectSubscription()
                .assertNext(sse -> {
                    assertThat(sse.data()).isEqualTo(statuses);
                }).thenCancel().verify();
    }

    @Test
    public void it_should_return_unauthorized_if_host_id_null() throws Exception {
        when(httpSession.getAttribute("hostId")).thenReturn(null);

        webClient.get().uri(url).accept(MediaType.ALL).exchange().expectStatus()
                .isUnauthorized();
    }
}
