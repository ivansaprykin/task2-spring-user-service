package com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.handlers;

import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.ApiError;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserDoesNotExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserWithSuchEmailAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class UserControllerExceptionHandler {

    @ExceptionHandler(UserDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError userDoesNotExistExceptionHandler(UserDoesNotExistException ex) {

        String message = ex.getMessage();
        return new ApiError(message);
    }

    @ExceptionHandler(UserWithSuchEmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError userWithSuchEmailAlreadyExistExceptionHandler(UserWithSuchEmailAlreadyExistException ex) {
        String message = ex.getMessage();
        return new ApiError(message);
    }
}
