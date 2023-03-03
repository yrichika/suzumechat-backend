package com.example.suzumechat.service.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.example.suzumechat.config.SecurityConfig;
import com.example.suzumechat.testconfig.TestConfig;


@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(UtilityController.class)
public class UtilityControllerTests {

    @Autowired
    private MockMvc mvc;

    // TODO: csrf token test
}
