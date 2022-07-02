package com.example.suzumechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class JavaConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
