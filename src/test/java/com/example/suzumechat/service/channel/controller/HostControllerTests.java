package com.example.suzumechat.service.channel.controller;

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
import com.example.suzumechat.service.channel.ChannelService;
import com.example.suzumechat.service.channel.form.VisitorsAuthStatus;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.form.VisitorsAuthStatusFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
    public void approveRequest_should_return_204_if_request_either_accepted_or_rejected()
            throws Exception {
        val hostChannelToken = testRandom.string.alphanumeric();
        val url = "/host/approveRequest/" + hostChannelToken;

        final VisitorsAuthStatus form = statusFactory.make();
        val jsonedForm = objectMapper.writeValueAsString(form);

        // Not necessary, but just as a memo.
        // doNothing().when(service).approveVisitor(form.getVisitorId(),
        // form.getIsAuthenticated());

        val request =
                post(url).contentType(MediaType.APPLICATION_JSON).content(jsonedForm)
                        .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(status().isNoContent());

        verify(service, times(1)).approveVisitor(form.getVisitorId(),
                form.getIsAuthenticated());


    }

}
