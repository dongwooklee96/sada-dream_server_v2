package com.sadadream.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.sadadream.domain.User;
import com.sadadream.domain.UserRepository;
import com.sadadream.dto.Gender;
import com.sadadream.errors.UserNotFoundException;

@DataJpaTest
@TestPropertySource("classpath:application-test.yml")
class BaseTimeTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저를 저장할 때, 생성 및 수정 시각이 제대로 기록된다.")
    @Test
    public void whenSavingUserCreateTimeAndUpdateTime() {
        // given
        LocalDateTime now = LocalDateTime.now();

        userRepository.save(User.builder()
            .id(1L)
            .email("example@test.com")
            .name("이동욱")
            .address("서울특별시 마포구 상암동 누리꿈 스퀘어")
            .phoneNumber("010-4444-4444")
            .gender(Gender.M)
            .birthDate(LocalDate.of(1996, 1, 6))
            .build());

        // when
        User user = userRepository.findById(1L)
            .orElseThrow(() -> new UserNotFoundException(1L));

        // then
        assertThat(user.getCreatedAt()).isAfter(now);
        assertThat(user.getUpdateAt()).isAfter(now);
    }
}
