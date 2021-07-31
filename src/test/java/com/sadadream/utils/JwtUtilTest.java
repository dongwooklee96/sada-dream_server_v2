package com.sadadream.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sadadream.errors.InvalidTokenException;

import io.jsonwebtoken.Claims;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    // userId = 1
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @DisplayName("특정 유저에 대한 토큰을 발행하였을 때 유효한 토큰값을 반환한다.")
    @Test
    void encode() {
        String token = jwtUtil.encode(1L);
        assertThat(token).isEqualTo(VALID_TOKEN);
    }

    @DisplayName("유효한 토큰의 경우, 설정한 값이 들어있다.")
    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(VALID_TOKEN);

        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
    }

    @DisplayName("토큰 값이 유효하지 않은 토큰의 경우 예외가 발생한다.")
    @Test
    void decodeWithInvalidToken() {
        assertThatThrownBy(() -> jwtUtil.decode(INVALID_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("비어있거나 토큰 형식이 올바르지 않은 토큰의 경우 예외가 발생한다.")
    @Test
    void decodeWithEmptyToken() {
        assertThatThrownBy(() -> jwtUtil.decode(null))
            .isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> jwtUtil.decode(""))
            .isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> jwtUtil.decode(" "))
            .isInstanceOf(InvalidTokenException.class);
    }
}
