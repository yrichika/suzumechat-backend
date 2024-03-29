package com.example.suzumechat.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.ApprovalResultFactory;
import com.example.suzumechat.testutil.stub.factory.dto.AuthenticationStatusFactory;
import com.example.suzumechat.testutil.stub.factory.dto.ChannelStatusFactory;
import com.example.suzumechat.testutil.stub.factory.dto.CreatedChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.GuestChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.GuestDtoFactory;
import com.example.suzumechat.testutil.stub.factory.dto.HostChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.JoinRequestFactory;
import com.example.suzumechat.testutil.stub.factory.dto.VisitorAuthStatusFactory;
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

    @Bean
    public ChannelStatusFactory channelStatusFactory() {
        return new ChannelStatusFactory();
    }

    @Bean
    public JoinRequestFactory joinRequestFactory() {
        return new JoinRequestFactory();
    }

    @Bean
    public VisitorAuthStatusFactory visitorsAuthStatusFactory() {
        return new VisitorAuthStatusFactory();
    }

    @Bean
    public AuthenticationStatusFactory authenticationStatusFactory() {
        return new AuthenticationStatusFactory();
    }

    @Bean
    public GuestChannelFactory guestChannelFactory() {
        return new GuestChannelFactory();
    }

    @Bean
    public GuestDtoFactory guestDtoFactory() {
        return new GuestDtoFactory();
    }

    @Bean
    public ApprovalResultFactory approvalResultFactory() {
        return new ApprovalResultFactory();
    }

    // Security customizations for tests
    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    // return (web) -> web.ignoring()
    // .antMatchers("/test");
    // }
}
