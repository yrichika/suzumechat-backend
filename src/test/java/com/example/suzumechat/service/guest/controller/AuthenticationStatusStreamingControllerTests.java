package com.example.suzumechat.service.guest.controller;

import static org.mockito.Mockito.when;
import java.time.Duration;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.guest.GuestService;
import com.example.suzumechat.service.guest.dto.AuthenticationStatus;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.AuthenticationStatusFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;

@Import({TestConfig.class, SecurityConfig.class})
@WebFluxTest(controllers = AuthenticationStatusStreamingController.class,
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@MockitoSettings
public class AuthenticationStatusStreamingControllerTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private GuestService guestService;
    @MockBean
    private HttpSession httpSession;

    @Autowired
    private TestRandom random;
    @Autowired
    private AuthenticationStatusFactory factory;

    private static final ParameterizedTypeReference<ServerSentEvent<AuthenticationStatus>> typeRef =
            new ParameterizedTypeReference<>() {};

    private final String urlPrefix = "/visitor/joinStatus/";
    private String joinChannelToken;
    private String url;

    @BeforeEach
    private void setUp() {
        joinChannelToken = random.string.alphanumeric();
        url = urlPrefix + joinChannelToken;
    }

    @Test
    public void test_it_should_return_sse_of_authentication_status()
            throws Exception {
        val visitorId = random.string.alphanumeric();
        final AuthenticationStatus expected = factory.make();
        when(httpSession.getAttribute("visitorId")).thenReturn(visitorId);
        when(guestService.getAuthenticationStatus(joinChannelToken, visitorId))
                .thenReturn(expected);

        FluxExchangeResult<ServerSentEvent<AuthenticationStatus>> result =
                webClient.get().uri(url).accept(MediaType.ALL).exchange()
                        .expectStatus().isOk().returnResult(typeRef);

        StepVerifier.create(result.getResponseBody()).expectSubscription()
                .assertNext(sse -> {
                    assertThat(sse.data()).isEqualTo(expected);
                }).thenCancel().verify();
    }


    @Test
    public void test_it_should_return_unauthorized_if_session_visitorId_is_null()
            throws Exception {
        when(httpSession.getAttribute("visitorId")).thenReturn(null);

        webClient.get().uri(url).accept(MediaType.ALL).exchange().expectStatus()
                .isUnauthorized();

    }

}
