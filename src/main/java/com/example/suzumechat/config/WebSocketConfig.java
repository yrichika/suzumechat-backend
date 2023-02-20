package com.example.suzumechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${ws.broadcast-prefix}")
    private String webSocketBroadcastPrefix;

    @Value("${ws.send-prefix}")
    private String webSocketSendPrefix;

    @Value("${ws.endpoint}")
    private String webSocketEndpoint;

    @Value("${front.url}")
    private String frontUrl;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // messages are broadcasted to destination begins with /topic, and so on.
        config.enableSimpleBroker(webSocketBroadcastPrefix);

        // destination header begins with this route will be routed to
        // @MessageMapping
        config.setApplicationDestinationPrefixes(webSocketSendPrefix);

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket handshake entry point
        registry.addEndpoint(webSocketEndpoint)
            // to use HttpSession from WebSocket
            .addInterceptors(new HttpHandshakeInterceptor())
            .setAllowedOrigins(frontUrl).withSockJS();
    }

}
