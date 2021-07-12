package com.sadadream.controllers;

import javax.validation.Valid;

import com.sadadream.application.AuthenticationService;
import com.sadadream.dto.SessionRequestData;
import com.sadadream.dto.SessionResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerErrorException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/session", produces = "application/json")
@CrossOrigin
@Slf4j
public class SessionController {
    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ApiOperation(value="로그인", notes = "로그인을 수행한다", response = SessionResponseData.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseData login(
            @Valid @RequestBody SessionRequestData sessionRequestData
    ) {
        String email = sessionRequestData.getEmail();
        String password = sessionRequestData.getPassword();

        String accessToken = authenticationService.login(email, password);

        return SessionResponseData.builder()
                .accessToken(accessToken)
                .build();
    }
}
