package com.example.suzumechat.service.utility;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.service.utility.UtilityController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringJUnitConfig
@ContextConfiguration(classes = SecurityConfig.class)
@Import(UtilityController.class)
@WebMvcTest(controllers = UtilityController.class)
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
