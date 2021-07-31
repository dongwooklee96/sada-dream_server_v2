package com.sadadream.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.github.dozermapper.core.Mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserModificationData {
    @NotBlank
    @Mapping("name")
    private final String name;

    @Mapping("address")
    private final String address;

    @Mapping("gender")
    private final Gender gender;
}
