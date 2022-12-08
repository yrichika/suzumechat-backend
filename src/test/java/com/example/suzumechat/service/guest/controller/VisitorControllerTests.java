package com.example.suzumechat.service.guest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.guest.application.VisitorUseCase;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.ChannelStatusFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(VisitorController.class)
@MockitoSettings
public class VisitorControllerTests {
    @MockBean
    private VisitorUseCase useCase;
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

    @Test
    public void channelName_should_return_channelName_if_exist() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val url = "/visitor/channelName/" + joinChannelToken;
        val channelStatus = channelStatusFactory.make();
        when(useCase.getChannelStatusByJoinChannelToken(joinChannelToken))
            .thenReturn(channelStatus);

        val expected = objectMapper.writeValueAsString(channelStatus);

        val request = get(url);
        mockMvc.perform(request).andExpect(status().isOk())
            .andExpect(content().json(expected));
    }

}
