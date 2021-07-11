package com.sadadream.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.github.dozermapper.core.Mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRegistrationData {
    @NotBlank
    @Email
    @Mapping("email")
    private final String email;

    @NotBlank
    @Mapping("name")
    private final String name;

    @NotBlank
    @Size(min = 8, max = 16)
    @Mapping("password")
    private final String password;

    @NotBlank
    @Mapping("phoneNumber")
    private final String phoneNumber;

    @NotBlank
    @Mapping("address")
    private final String address;

    @Mapping("gender")
    private final Gender gender;

    @Mapping("birthDate")
    private final LocalDate birthDate;
}
