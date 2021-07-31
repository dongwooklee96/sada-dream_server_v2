package com.sadadream.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sadadream.domain.Role;
import com.sadadream.domain.RoleRepository;
import com.sadadream.domain.User;
import com.sadadream.domain.UserRepository;
import com.sadadream.dto.UserModificationData;
import com.sadadream.dto.UserRegistrationData;
import com.sadadream.errors.UserEmailDuplicationException;
import com.sadadream.errors.UserNotFoundException;

class UserServiceTest {
    private static final String EXISTED_EMAIL_ADDRESS = "existed@example.com";
    private static final Long DELETED_USER_ID = 200L;

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(mapper, userRepository, roleRepository, passwordEncoder);

        given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                .willReturn(true);

        given(userRepository.save(any(User.class))).will(invocation -> {
            User source = invocation.getArgument(0);
            return User.builder()
                    .id(13L)
                    .email(source.getEmail())
                    .name(source.getName())
                    .build();
        });

        given(userRepository.findByIdAndDeletedIsFalse(1L))
                .willReturn(Optional.of(
                        User.builder()
                                .id(1L)
                                .email(EXISTED_EMAIL_ADDRESS)
                                .name("Tester")
                                .password("test")
                                .build()));

        given(userRepository.findByIdAndDeletedIsFalse(100L))
                .willReturn(Optional.empty());

        given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                .willReturn(Optional.empty());
    }

    @DisplayName("유저 생성 요청을 하면, 정상적으로 생성된다.")
    @Test
    void registerUser() {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .email("tester@example.com")
                .name("박재성")
                .password("test")
                .build();

        User user = userService.registerUser(registrationData);

        assertThat(user.getId()).isEqualTo(13L);
        assertThat(user.getEmail()).isEqualTo("tester@example.com");
        assertThat(user.getName()).isEqualTo("박재성");

        verify(userRepository).save(any(User.class));
        verify(roleRepository).save(any(Role.class));
    }

    @DisplayName("중복된 이메일로 유저 생성 요청을 하면, 예외가 발생한다.")
    @Test
    void registerUserWithDuplicatedEmail() {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .email(EXISTED_EMAIL_ADDRESS)
                .name("Tester")
                .password("test")
                .build();

        assertThatThrownBy(() -> userService.registerUser(registrationData))
                .isInstanceOf(UserEmailDuplicationException.class);

        verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
    }

    @DisplayName("존재하는 유저 아이디로, 수정을 요청하면 정상적으로 수정된다.")
    @Test
    void updateUserWithExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("박재성")
                .build();

        Long userId = 1L;
        User user = userService.updateUser(1L, modificationData, userId);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);

        assertThat(user.getName()).isEqualTo("박재성");

        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @DisplayName("존재하지 않는 유저 아이디로 유저 정보 수정을 요청하면 예외가 발생한다.")
    @Test
    void updateUserWithNotExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .build();

        Long userId = 100L;
        assertThatThrownBy(() -> userService.updateUser(100L, modificationData, userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(100L);
    }

    @DisplayName("삭제된 유저 아이디로, 유저 정보 수정을 요청하면 예외가 발생한다.")
    @Test
    void updateUserWithDeletedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .build();

        Long userId = DELETED_USER_ID;
        assertThatThrownBy(
                () -> userService.updateUser(DELETED_USER_ID, modificationData, userId)
        )
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }

    @DisplayName("다른 유저의 액세스 토큰으로 유저 정보 수정을 요청하면 예외가 발생한다.")
    @Test
    void updateUserByOthersAccess() {
        UserModificationData modificationData = UserModificationData.builder()
            .name("TEST")
            .build();

        Long targetUserId = 1L;
        Long currentUserId = 2L;

        assertThatThrownBy(() -> userService.updateUser(
            targetUserId, modificationData, currentUserId)).isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("존재하는 유저 아이디를 삭제하면 정상적으로 삭제된다.")
    @Test
    void deleteUserWithExistedId() {
        User user = userService.deleteUser(1L);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.isDeleted()).isTrue();

        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @DisplayName("존재하지 않는 유저 아이디로 삭제를 시도하면 예외가 발생한다.")
    @Test
    void deleteUserWithNotExistedId() {
        assertThatThrownBy(() -> userService.deleteUser(100L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(100L);
    }

    @DisplayName("삭제된 유저 아이디로 삭제를 시도하면 예외가 발생한다.")
    @Test
    void deleteUserWithDeletedId() {
        assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }
}
