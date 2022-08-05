package com.example.suzumechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:settings.properties")
public class SuzumechatApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuzumechatApplication.class, args);
	}

}
