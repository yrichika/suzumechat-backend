package com.example.suzumechat.service.guest.controller;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.Mockito.*;

import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.form.JoinRequest;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.ChannelStatusFactory;
import com.example.suzumechat.testutil.stub.factory.form.JoinRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.suzumechat.service.guest.dto.ReceptionStatus;

import lombok.val;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(VisitorController.class)
@MockitoSettings
public class VisitorControllerTests {
    @MockBean
    private GuestService service;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRandom testRandom;
    @Autowired
    private ChannelStatusFactory channelStatusFactory;
    @Autowired
    private JoinRequestFactory joinRequestFactory;

    @Test
    public void channelName_should_return_channelName_if_exist() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val url = "/visitor/channelName/" + joinChannelToken;
        val channelStatus = channelStatusFactory.make();
        when(service.getChannelNameByJoinChannelToken(joinChannelToken))
                .thenReturn(channelStatus);

        val expected = objectMapper.writeValueAsString(channelStatus);

        val request = get(url);
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    public void joinRequest_should_create_visitor_and_return_any_string_except_closed_status()
            throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val url = "/visitor/joinRequest/" + joinChannelToken;
        final JoinRequest joinRequest = joinRequestFactory.make();
        final String form = objectMapper.writeValueAsString(joinRequest);
        val visitorId = "fakeVisitorId";

        when(service.createGuestAsVisitor(joinChannelToken, visitorId,
                joinRequest.getCodename(), joinRequest.getPassphrase()))
                        .thenReturn(Optional.of(visitorId));

        val request = post(url).contentType(MediaType.APPLICATION_JSON).content(form)
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        val expected = objectMapper.writeValueAsString(new ReceptionStatus(true));
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(request().sessionAttribute("visitorId", visitorId))
                .andExpect(content().json(expected));
    }

    @Test
    public void joinRequest_should_not_set_session_value_if_channel_already_closed_and_return_closed_status_string()
            throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val url = "/visitor/joinRequest/" + joinChannelToken;
        final JoinRequest joinRequest = joinRequestFactory.make();
        final String form = objectMapper.writeValueAsString(joinRequest);
        val visitorId = testRandom.string.alphanumeric();

        when(service.createGuestAsVisitor(joinChannelToken, visitorId,
                joinRequest.getCodename(), joinRequest.getPassphrase()))
                        .thenReturn(Optional.empty());

        val request = post(url).contentType(MediaType.APPLICATION_JSON).content(form)
                .with(SecurityMockMvcRequestPostProcessors.csrf());


        val expected = objectMapper.writeValueAsString(new ReceptionStatus(false));
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(request().sessionAttribute("visitorId", nullValue()))
                .andExpect(content().json(expected));
    }
}
