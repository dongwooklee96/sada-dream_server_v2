package com.sadadream.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import com.sadadream.application.AuthenticationService;
import com.sadadream.application.UserService;
import com.sadadream.domain.Role;
import com.sadadream.domain.User;
import com.sadadream.dto.UserModificationData;
import com.sadadream.dto.UserRegistrationData;
import com.sadadream.errors.UserNotFoundException;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

    // userId = 1
    private static final String MY_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
        "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    // userId = 2
    private static final String OTHER_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
        "eyJ1c2VySWQiOjJ9.TEM6MULsZeqkBbUKziCR4Dg_8kymmZkyxsCXlfNJ3g0";
    // userId = 3
    private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
        "eyJ1c2VySWQiOjEwMDR9.3GV5ZH3flBf0cnaXQCNNZlT4mgyFyBUhn3LKzQohh1A";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(userService.registerUser(any(UserRegistrationData.class)))
            .will(invocation -> {
                UserRegistrationData registrationData =
                    invocation.getArgument(0);
                return User.builder()
                    .id(13L)
                    .email(registrationData.getEmail())
                    .name(registrationData.getName())
                    .address(registrationData.getAddress())
                    .build();
            });

        given(
            userService.updateUser(
                eq(1L),
                any(UserModificationData.class),
                eq(1L)
            )
        )
            .will(invocation -> {
                Long id = invocation.getArgument(0);
                UserModificationData modificationData =
                    invocation.getArgument(1);
                return User.builder()
                    .id(id)
                    .email("tester@example.com")
                    .name(modificationData.getName())
                    .build();
            });

        given(
            userService.updateUser(
                eq(100L),
                any(UserModificationData.class),
                eq(1L)
            )
        )
            .willThrow(new UserNotFoundException(100L));

        given(
            userService.updateUser(
                eq(1L),
                any(UserModificationData.class),
                eq(2L)
            )
        )
            .willThrow(new AccessDeniedException("Access denied"));

        given(userService.deleteUser(100L))
            .willThrow(new UserNotFoundException(100L));

        given(authenticationService.parseToken(MY_TOKEN)).willReturn(1L);
        given(authenticationService.parseToken(OTHER_TOKEN)).willReturn(2L);
        given(authenticationService.parseToken(ADMIN_TOKEN)).willReturn(1004L);

        given(authenticationService.roles(1L))
            .willReturn(Arrays.asList(new Role("USER")));
        given(authenticationService.roles(2L))
            .willReturn(Arrays.asList(new Role("USER")));
        given(authenticationService.roles(1004L))
            .willReturn(Arrays.asList(new Role("USER"), new Role("ADMIN")));
    }

    @DisplayName("올바른 형식으로 유저 생성을 요청하였을 떄, 유저 생성 및 올바른 상태코드가 반환된다")
    @Test
    void registerUserWithValidAttributes() throws Exception {
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"address\": \"서울특별시 흑석로 15\",\n"
                    + "  \"birthDate\": \"1996-01-06\",\n"
                    + "  \"email\": \"tester@example.com\",\n"
                    + "  \"gender\": \"M\",\n"
                    + "  \"name\": \"tester\",\n"
                    + "  \"password\": \"pass1234\",\n"
                    + "  \"phoneNumber\": \"pass1234\"\n"
                    + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(
                        containsString("\"id\":13")
                ))
                .andExpect(content().string(
                        containsString("\"email\":\"tester@example.com\"")
                ))
                .andExpect(content().string(
                        containsString("\"name\":\"tester\"")
                ));

        verify(userService).registerUser(any(UserRegistrationData.class));
    }

    @DisplayName("올바르지 않은 형식으로 유저 생성 요청을 하면, 유저 생성이 되지 않는다.")
    @Test
    void registerUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("올바른 형식으로 유저 수정 요청을 하면 정상적으로 수행된다.")
    @Test
    void updateUserWithValidAttributes() throws Exception {
        mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NAME\"}")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
            .andExpect(status().isOk())
            .andExpect(content().string(
                containsString("\"id\":1")
            ))
            .andExpect(content().string(
                containsString("\"name\":\"NAME\"")
            ));

        verify(userService)
            .updateUser(eq(1L), any(UserModificationData.class), eq(1L));
    }

    @DisplayName("유효하지 않은 형식으로 유저 수정 요청을 하면, 올바르지 않은 요청이라고 한다. (400)")
    @Test
    void updateUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"\"}")
                    .header("Authorization", "Bearer" + MY_TOKEN)

        )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("존재하지 않는 유저에 대해서 유저 수정 요청을 하면, 찾을 수 없다. (404)")
    @Test
    void updateUserWithNotExistedId() throws Exception {
        mockMvc.perform(
            patch("/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
            .andExpect(status().isNotFound());

        verify(userService).updateUser(
            eq(100L),
            any(UserModificationData.class),
            eq(1L));
    }

    @DisplayName("액세스 토큰 없이 유저 수정 요청을 하면, 허가되지 않는다. (401)")
    @Test
    void updateUserWithoutAccessToken() throws Exception {
        mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"이동욱\",\"password\":\"passw0rd\"}")
        )
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("다른 유저의 액세스 토큰으로  유저 수정 요청을 하면, 금지된다. (403)")
    @Test
    void updateUserWithOthersAccessToken() throws Exception {
        mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"이동욱\",\"password\":\"passw0rd\"}")
                .header("Authorization", "Bearer " + OTHER_TOKEN)
        )
            .andExpect(status().isForbidden());

        verify(userService)
            .updateUser(eq(1L), any(UserModificationData.class), eq(2L));
    }

    @DisplayName("존재하는 유저에 대해서 유저 삭제 요청을 하면, 정상적으로 삭제가 이루어진다. (204)")
    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(
            delete("/users/1")
                .header("Authorization", "Bearer " + ADMIN_TOKEN)
        )
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @DisplayName("존재 하지 않는 유저에 대해서 유저 삭제 요청을 하면, 찾을 수 없다. (404)")
    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(
            delete("/users/100")
                .header("Authorization", "Bearer " + ADMIN_TOKEN)
        )
            .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }

    @DisplayName("엑세스 토큰 없이 유저 삭제 요청을 하면, 허가되지 않는다. (401)")
    @Test
    void destroyWithoutAccessToken() throws Exception {
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("관리자 권한이 없는 유저가 유저 삭제 요청을 하면, 금지 된다. (403)")
    @Test
    void destroyWithoutAdminRole() throws Exception {
        mockMvc.perform(
            delete("/users/1")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
            .andExpect(status().isForbidden());
    }
}
