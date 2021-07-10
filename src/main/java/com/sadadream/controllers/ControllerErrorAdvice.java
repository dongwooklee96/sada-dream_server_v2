package com.sadadream.controllers;

import com.sadadream.dto.ErrorResponse;
import com.sadadream.errors.LoginFailException;
import com.sadadream.errors.ProductNotFoundException;
import com.sadadream.errors.UserEmailDuplicationException;
import com.sadadream.errors.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.Date;
import java.util.Set;

@ResponseBody
@ControllerAdvice
public class ControllerErrorAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorResponse handleProductNotFound(Exception exception, WebRequest request) {
        return new ErrorResponse(new Date(), exception.getMessage(), request.getDescription(false));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(Exception exception, WebRequest request) {
        return new ErrorResponse(new Date(), exception.getMessage(), request.getDescription(false));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public ErrorResponse handleUserEmailIsAlreadyExisted(Exception exception, WebRequest request) {
        return new ErrorResponse(new Date(), exception.getMessage(), request.getDescription(false));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException(Exception exception, WebRequest request) {
        return new ErrorResponse(new Date(), exception.getMessage(), request.getDescription(false));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintValidateError(
            ConstraintViolationException exception, WebRequest request
    ) {
        String messageTemplate = getViolatedMessage(exception);
        return new ErrorResponse(new Date(), messageTemplate, request.getDescription(false));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidError(Exception exception, WebRequest request) {
        return new ErrorResponse(new Date(), exception.getMessage(), request.getDescription(false));
    }

    private String getViolatedMessage(ConstraintViolationException exception) {
        String messageTemplate = null;
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            messageTemplate = violation.getMessageTemplate();
        }
        return messageTemplate;
    }
}
