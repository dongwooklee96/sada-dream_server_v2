package com.sadadream.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel(description = "로그인 요청 리퀘스트")
@Getter
public class SessionRequestData {
    @ApiModelProperty(required = true, value = "이메일")
    @Email
    private String email;

    @ApiModelProperty(required = true, value = "패스워드")
    @NotBlank
    private String password;
}
