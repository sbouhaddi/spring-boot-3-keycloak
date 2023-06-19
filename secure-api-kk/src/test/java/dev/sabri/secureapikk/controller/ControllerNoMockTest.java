package dev.sabri.secureapikk.controller;


import dev.sabri.oauthkk.config.JwtAuthConverterProperties;
import dev.sabri.oauthkk.config.TestSecurityConfig;
import dev.sabri.oauthkk.utils.TestJWTUtils;
import dev.sabri.secureapikk.api.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import({JwtAuthConverterProperties.class, TestSecurityConfig.class})
class ControllerNoMockTest {


    @Autowired
    private MockMvc mockMvc;
    private static final String API_URL = "/api/test/{user}";

    @Test
    void testGetUserWithRandomToken() throws Exception {
        mockMvc.perform(get(API_URL, "user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk());
    }
}
