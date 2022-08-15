package com.example.suzumechat.config;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

// REF: https://www.devglan.com/spring-boot/spring-session-stomp-websocket
// This is an event lister to check HttpSession during WebSocket connection.
// @Component // uncomment here to enable this.
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

	@Override
	public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
		System.out.println(headerAccessor.getSessionAttributes().get("sessionId").toString());
	}
}
