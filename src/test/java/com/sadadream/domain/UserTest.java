package com.sadadream.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sadadream.dto.Gender;

class UserTest {

    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("example@test.com")
            .name("이동욱")
            .address("서울특별시 마포구 상암동 누리꿈 스퀘어")
            .phoneNumber("010-4444-4444")
            .gender(Gender.M)
            .birthDate(LocalDate.of(1996, 1, 6))
            .build();

        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @DisplayName("유저를 수정하였을 때, 변경사항이 정상적으로 적용된다.")
    @Test
    void changeWith() {
        // when
        user.changeWith(User.builder()
            .name("박재성")
            .address("경기도 안양시 뜨란채 아파트")
            .phoneNumber("010-1234-5678")
            .gender(Gender.M)
            .birthDate(LocalDate.of(1996, 5, 5))
            .build());
        // then
        assertThat(user.getName()).isEqualTo("박재성");
        assertThat(user.getAddress()).isEqualTo("경기도 안양시 뜨란채 아파트");
        assertThat(user.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(user.getGender()).isEqualTo(Gender.M);
        assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(1996, 5, 5));
    }

    @DisplayName("패스워드를 변경하였을 때, 정상적으로 변경이 된다.")
    @Test
    void changePassword() {
        String oldPassword = user.getPassword();

        user.changePassword("CHANGE_PASSWORD", passwordEncoder);

        assertThat(user.getPassword()).isNotEqualTo(oldPassword);
    }

    @DisplayName("유저의 패스워드를 변경하였을 때, 변경한 패스워드로 인증을 하면 성공한다.")
    @Test
    void authenticate() {
        user.changePassword("CHANGE_PASSWORD", passwordEncoder);

        assertThat(user.authenticate("CHANGE_PASSWORD", passwordEncoder)).isTrue();
        assertThat(user.authenticate("xxx", passwordEncoder)).isFalse();
    }

    @DisplayName("삭제한 유저를 인증할 때 비밀번호가 일치하던지 아니던지 인증에 실패한다.")
    @Test
    void authenticateWithDeletedUser() {
        User deletedUser = User.builder()
            .deleted(true)
            .build();

        user.changePassword("CHANGE_PASSWORD", passwordEncoder);

        assertThat(deletedUser.authenticate("CHANGE_PASSWORD", passwordEncoder)).isFalse();
        assertThat(deletedUser.authenticate("INVALID_PASSWORD", passwordEncoder)).isFalse();
    }

    @DisplayName("유저를 삭제하면, 유저의 삭제 여부가 참으로 변경된다.")
    @Test
    void destroy() {
        assertThat(user.isDeleted()).isFalse();

        user.destroy();

        assertThat(user.isDeleted()).isTrue();
    }
}
