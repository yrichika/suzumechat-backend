package com.example.suzumechat.config;

import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

// REF: https://www.devglan.com/spring-boot/spring-session-stomp-websocket
// This is to maintain HttpSession during WebSocket connection.
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest =
                    (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
            attributes.put("hostId", session.getAttribute("hostId"));
            attributes.put("secretKeyHost", session.getAttribute("secretKeyHost"));
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        //
    }

}
