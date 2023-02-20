package com.example.suzumechat.service.channel.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;

@Disabled
@Import({TestConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@MockitoSettings
public class WebSocketMessageControllerTests {

    @Autowired
    private Environment env;
    @Autowired
    private TestRandom random;
    @MockBean
    private ChannelService service;

    @LocalServerPort
    public int port;

    @Value("${ws.endpoint}")
    private String wsEndpoint;

    private CompletableFuture<String> completableFuture;

    // REF:
    // https://medium.com/@MelvinBlokhuijzen/spring-websocket-endpoints-integration-testing-180357b4f24c
    @Disabled // TODO: This test is incomplete. I have no idea how to test WebSocket
    // functionalities.
    @Test
    public void receiveAndBroadcast_should_receive_message_and_broadcast_to_host_and_guests()
        throws Exception {


        WebSocketStompClient client = new WebSocketStompClient(
            new SockJsClient(createTransportClient()));
        client.setMessageConverter(new MappingJackson2MessageConverter());
        val url = "ws://localhost:" + port + wsEndpoint;

        // StompSession session = client.connect(url, new
        // StompSessionHandlerAdapter() {})
        // .get(1, TimeUnit.SECONDS);

        // val hostChannelToken = random.string.alphanumeric();
        // val guestChannelToken = random.string.alphanumeric();

        // session.subscribe("/receive/host/" + hostChannelToken, new
        // CreateStompFrameHandler());

        // // session.subscribe("/receive/guest/" + guestChannelToken, new
        // CreateStompFrameHandler());

        // session.send("/send/host/" + hostChannelToken, "some message");

        // val message = completableFuture.get(10, TimeUnit.SECONDS);

        // assertThat(message).isNotEmpty();
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }


    private class CreateStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((String) o);
        }
    }
}
