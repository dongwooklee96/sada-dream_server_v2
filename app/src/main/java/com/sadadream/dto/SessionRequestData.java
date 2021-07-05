package com.sadadream.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel(description = "로그인 요청 리퀘스트")
@Getter
public class SessionRequestData {
    @ApiModelProperty(required = true, value = "이메일")
    private String email;
    @ApiModelProperty(required = true, value = "패스워드")
    private String password;
}
