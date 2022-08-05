package com.example.suzumechat.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.CreatedChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.HostChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import com.example.suzumechat.testutil.stub.factory.form.CreatingChannelFactory;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public ChannelFactory channelFactory() {
        return new ChannelFactory();
    }

    @Bean
    public GuestFactory guestFactory() {
        return new GuestFactory();
    }

    @Bean
    public TestRandom testRandom() {
        return new TestRandom();
    }

    @Bean
    public CreatingChannelFactory creatingChannelFactory() {
        return new CreatingChannelFactory();
    }

    @Bean
    public HostChannelFactory hostChannelFactory() {
        return new HostChannelFactory();
    }

    @Bean
    public CreatedChannelFactory createdChannelFactory() {
        return new CreatedChannelFactory();
    }

    // Security customizations for tests
    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     return (web) -> web.ignoring()
    //         .antMatchers("/test");
    // }
}
