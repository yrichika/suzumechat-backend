package com.example.suzumechat.service.channel.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.channel.application.ChannelUseCase;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.stub.factory.dto.CreatedChannelFactory;
import com.example.suzumechat.testutil.stub.factory.form.CreatingChannelFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(ChannelController.class)
@MockitoSettings
public class ChannelControllerTests {

    @MockBean
    private ChannelUseCase service;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreatingChannelFactory formFactory;
    @Autowired
    private CreatedChannelFactory createdChannelFactory;

    @Test
    public void create_should_set_session_data_and_return_chat_channel()
            throws Exception {
        val url = "/createChannel";

        val form = formFactory.make();
        String json = objectMapper.writeValueAsString(form);

        val createdChannel = createdChannelFactory.make();
        when(service.create(form.getChannelName())).thenReturn(createdChannel);
        val expectedHostChannel =
                objectMapper.writeValueAsString(createdChannel.hostChannel());

        val request = post(url).contentType(MediaType.APPLICATION_JSON).content(json)
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(request().sessionAttribute("hostId",
                        createdChannel.hostId()))
                .andExpect(request().sessionAttribute("secretKeyHost",
                        createdChannel.hostChannel().secretKey()))
                .andExpect(content().json(expectedHostChannel));
    }
}
