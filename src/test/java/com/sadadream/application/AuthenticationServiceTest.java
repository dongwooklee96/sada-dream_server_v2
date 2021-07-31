package com.sadadream.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sadadream.domain.Role;
import com.sadadream.domain.RoleRepository;
import com.sadadream.domain.User;
import com.sadadream.domain.UserRepository;
import com.sadadream.errors.InvalidTokenException;
import com.sadadream.errors.LoginFailException;
import com.sadadream.utils.JwtUtil;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
        "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
        "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private AuthenticationService authenticationService;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(
            userRepository, roleRepository, jwtUtil, passwordEncoder);

        User user = User.builder()
            .id(1L)
            .build();

        user.changePassword("valid_password", passwordEncoder);

        given(userRepository.findByEmail("tester@example.com"))
            .willReturn(Optional.of(user));

        given(roleRepository.findAllByUserId(1L))
            .willReturn(Arrays.asList(new Role("USER")));
        given(roleRepository.findAllByUserId(1004L))
            .willReturn(Arrays.asList(new Role("USER"), new Role("ADMIN")));
    }

    @DisplayName("올바른 아이디와 패스워드로 로그인을 했을 때 성공한다.")
    @Test
    void loginWithRightEmailAndPassword() {
        String accessToken = authenticationService.login(
                "tester@example.com", "valid_password");

        assertThat(accessToken).isEqualTo(VALID_TOKEN);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @DisplayName("잘못된 이메일로 로그인을 시도 했을 때 예외가 발생한다.")
    @Test
    void loginWithWrongEmail() {
        assertThatThrownBy(
                () -> authenticationService.login("wrong@example.com", "test")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("wrong@example.com");
    }

    @DisplayName("잘못된 비밀번호로 로그인을 시도 했을 때 예외가 발생한다.")
    @Test
    void loginWithWrongPassword() {
        assertThatThrownBy(
                () -> authenticationService.login("tester@example.com", "xxx")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @DisplayName("특정 유저의 유효한 액세스 토큰을 파싱하면, 해당 유저의 정보를 반환한다.")
    @Test
    void parseTokenWithValidToken() {
        Long userId = authenticationService.parseToken(VALID_TOKEN);

        assertThat(userId).isEqualTo(1L);
    }

    @DisplayName("유효하지 않은 형식의 토큰을 파싱하면, 예외가 발생한다.")
    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(INVALID_TOKEN)
        ).isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("유저의 권한 정보를 조회하였을 때 유효한 권한 정보를 반환한다.")
    @Test
    void roles() {
        assertThat(authenticationService.roles(1L)
            .stream()
            .map(Role::getRole)
            .collect(Collectors.toList()))
            .isEqualTo(Arrays.asList("USER"));

        assertThat(authenticationService.roles(1004L)
            .stream()
            .map(Role::getRole)
            .collect(Collectors.toList()))
            .isEqualTo(Arrays.asList("USER", "ADMIN"));
    }
}
