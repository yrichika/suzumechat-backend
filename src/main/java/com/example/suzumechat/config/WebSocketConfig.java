package com.example.suzumechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    Environment env;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // messages are broadcasted to destination begins with /topic, and so on.
        config.enableSimpleBroker(env.getProperty("ws.broadcast-prefix"));

        // destination header begins with this route will be routed to
        // @MessageMapping
        config.setApplicationDestinationPrefixes(env.getProperty("ws.send-prefix"));

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket handshake entry point
        registry.addEndpoint(env.getProperty("ws.endpoint"))
                // to use HttpSession from WebSocket
                .addInterceptors(new HttpHandshakeInterceptor())
                .setAllowedOrigins(env.getProperty("front.url")).withSockJS();
    }

}
