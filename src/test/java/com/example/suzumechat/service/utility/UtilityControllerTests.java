package com.example.suzumechat.service.utility;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.testconfig.TestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(UtilityController.class)
public class UtilityControllerTests {
    
    @Autowired
    private MockMvc mvc;

    @Test
    public void testing_healthcheck_should_just_return_text_1() throws Exception
    {
        this.mvc.perform(get("/healthcheck").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }
}
