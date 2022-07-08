package com.example.suzumechat.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.random.pattern.RandomString;
import com.example.suzumechat.testutil.stub.factory.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.GuestFactory;

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


}
