package com.example.suzumechat.service.channel.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.channel.form.VisitorsAuthStatus;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.form.VisitorsAuthStatusFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import javax.servlet.http.HttpSession;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.Mockito.*;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(HostController.class)
@MockitoSettings
public class HostControllerTests {

    @MockBean
    private ChannelService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRandom testRandom;

    @Autowired
    VisitorsAuthStatusFactory statusFactory;

    @Test
    public void endChannel_should_delete_secretKey_and_invalidate_session()
            throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val url = "/host/endChannel/" + hostChannelToken;
        val hostId = testRandom.string.alphanumeric();

        val request = post(url).contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("hostId", hostId)
                .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(status().isNoContent())
                .andExpect(request().sessionAttributeDoesNotExist("hostId"));

        verify(service, times(1)).trashSecretKeyByHostChannelToken(hostId,
                hostChannelToken);
    }

    @Test
    public void endChannel_should_return_unauthorized_if_host_id_does_not_exist_in_session()
            throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val url = "/host/endChannel/" + hostChannelToken;

        val request = post(url).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

}
