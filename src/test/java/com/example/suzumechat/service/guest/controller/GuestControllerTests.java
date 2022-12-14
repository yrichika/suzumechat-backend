package com.example.suzumechat.service.guest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.guest.application.GuestChannelUseCase;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.GuestChannelFactory;
import com.example.suzumechat.testutil.stub.factory.dto.GuestDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(GuestController.class)
@MockitoSettings
public class GuestControllerTests {
    @MockBean
    private GuestChannelUseCase useCase;
    @Mock
    private HttpSession session;

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
    public void setSession_should_return_ok_if_guest_exist_in_the_channel()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/setSession/" + guestChannelToken;

        when(useCase.guestExistsInChannel(guestId, guestChannelToken))
            .thenReturn(true);

        val request = get(url).param("guestId", guestId);
        mockMvc.perform(request).andExpect(status().isOk())
            .andExpect(request().sessionAttribute("guestId", guestId));
    }

    @Test
    public void setSession_should_return_unauthorized() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/setSession/" + guestChannelToken;

        when(useCase.guestExistsInChannel(guestId, guestChannelToken))
            .thenReturn(false);

        val request = get(url).param("guestId", guestId);
        mockMvc.perform(request).andExpect(status().isUnauthorized())
            .andExpect(request().sessionAttributeDoesNotExist("guestId"));
    }

    @Test
    public void endChat_should_invalidate_session() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/endChat/" + guestChannelToken;

        when(useCase.guestExistsInChannel(guestId, guestChannelToken))
            .thenReturn(true);

        val request = get(url).sessionAttr("guestId", guestId);
        mockMvc.perform(request).andExpect(status().isNoContent())
            .andExpect(request().sessionAttributeDoesNotExist("guestId"));
    }

    @Test
    public void endChat_should_return_unauthorized_if_geust_id_does_not_exist_in_session()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/endChat/" + guestChannelToken;

        when(useCase.guestExistsInChannel(guestId, guestChannelToken))
            .thenReturn(true);

        val request = get(url);
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void endChat_should_return_unauthorized_if_guest_does_not_belong_to_channel()
        throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/endChat/" + guestChannelToken;

        when(useCase.guestExistsInChannel(guestId, guestChannelToken))
            .thenReturn(false); // NOTICE: difference

        val request = get(url).sessionAttr("guestId", guestId);
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    // DELETE:
    @Test
    public void channelName_should_return_guestChannel() throws Exception {
        val guestChannelToken = testRandom.string.alphanumeric();
        val url = "/guest/guestChannel/" + guestChannelToken;
        val guestChannel = guestChannelFactory.make();

        when(useCase.getGuestChannelByGuestChannelToken(guestChannelToken))
            .thenReturn(guestChannel);

        val expected = objectMapper.writeValueAsString(guestChannel);

        val request = get(url);
        mockMvc.perform(request).andExpect(status().isOk())
            .andExpect(content().json(expected));
    }

}
