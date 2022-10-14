package com.example.suzumechat.service.guest.controller;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.guest.application.GuestChannelService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.GuestChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.GuestDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(GuestController.class)
@MockitoSettings
public class GuestControllerTests {
    @MockBean
    private GuestChannelService service;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRandom testRandom;
    @Autowired
    private GuestChannelFactory guestChannelFactory;
    @Autowired
    private GuestDtoFactory guestDtoFactory;

    @Test
    public void channelName_should_return_guestChannel() throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/guestChannel/" + guestChannelToken;
        val guestChannel = guestChannelFactory.make();

        when(service.getGuestChannelByGuestChannelToken(guestChannelToken))
                .thenReturn(guestChannel);

        val expected = objectMapper.writeValueAsString(guestChannel);

        val request = get(url);
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    public void guestDto_should_return_guestDto_by_guestId() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/guestDto/" + guestChannelToken;
        val guestDto = guestDtoFactory.make();
        when(service.getGuestDtoByGuestId(guestId, guestChannelToken))
                .thenReturn(guestDto);

        val expected = objectMapper.writeValueAsString(guestDto);
        val request = get(url).param("guestId", guestId);
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(request().sessionAttribute("guestId", guestId))
                .andExpect(content().json(expected));
    }
}
