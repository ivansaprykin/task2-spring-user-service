package com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions;

public class UserWithSuchEmailAlreadyExistException extends Exception {

    public UserWithSuchEmailAlreadyExistException(String s) {
        super(s);
    }
}
