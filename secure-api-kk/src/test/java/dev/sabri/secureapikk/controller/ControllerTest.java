package dev.sabri.secureapikk.controller;

import dev.sabri.oauthkk.config.JwtAuthConverter;
import dev.sabri.oauthkk.config.JwtAuthConverterProperties;
import dev.sabri.oauthkk.config.WebSecurityConfig;
import dev.sabri.secureapikk.api.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import({JwtAuthConverterProperties.class, JwtAuthConverter.class, WebSecurityConfig.class})
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static final String API_URL = "/api/test/{user}";

    @Test
    @WithMockUser(authorities = {"user", "admin"})
    void testCreateBook() throws Exception {

        ResultActions resultActions = mockMvc.perform(get(API_URL, "user"))
                .andDo(print());
        resultActions.andExpect(status().isOk());
    }
}
