package com.example.suzumechat.config;

import org.springframework.web.socket.server.HandshakeInterceptor;
import lombok.val;
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

            // WARNING! this process is done in all websocket connection
            // host/guest/visitor all websocket connections go through this process
            // you don't wanna put unrelated values to different
            // actors(host/guest/visitor). if you do, it will throw exception
            // that's why all these values are checked before put into the
            // `attribute`

            attributes.put("sessionId", session.getId());

            val hostId = session.getAttribute("hostId");
            if (hostId != null) {
                attributes.put("hostId", hostId);
            }

            val secretKeyHost = session.getAttribute("secretKeyHost");
            if (secretKeyHost != null) {
                attributes.put("secretKeyHost", secretKeyHost);
            }
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        //
    }

}
