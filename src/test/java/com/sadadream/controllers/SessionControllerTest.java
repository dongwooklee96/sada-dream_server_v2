package com.sadadream.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sadadream.application.AuthenticationService;
import com.sadadream.errors.LoginFailException;

@WebMvcTest(SessionController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(authenticationService.login("tester@example.com", "test"))
                .willReturn("eyJhbGciOiJIUzI1NiJ9."
                    + "eyJ1c2VySWQiOjF9."
                    + "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk");

        given(authenticationService.login("badguy@example.com", "test"))
                .willThrow(new LoginFailException("badguy@example.com"));

        given(authenticationService.login("tester@example.com", "xxx"))
                .willThrow(new LoginFailException("tester@example.com"));
    }

    @DisplayName("유효한 이메일로, 로그인을 시도하면 성공한다.")
    @Test
    void loginWithRightEmailAndPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"tester@example.com\"," +
                        "\"password\":\"test\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(".")));

        verify(authenticationService).login(anyString(), anyString());
    }

    @DisplayName("잘못된 이메일로 로그인을 시도하면, 실패한다.")
    @Test
    void loginWithWrongEmail() throws Exception {
        mockMvc.perform(
                post("/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"badguy@example.com\"," +
                        "\"password\":\"test\"}")
        )
                .andExpect(status().isBadRequest());
        verify(authenticationService).login(anyString(), anyString());
    }

    @DisplayName("잘못된 패스워드로 로그인을 시도하면, 실패한다.")
    @Test
    void loginWithWrongPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"tester@example.com\"," +
                        "\"password\":\"xxx\"}")
        )
                .andExpect(status().isBadRequest());
        verify(authenticationService).login(anyString(), anyString());
    }
}
