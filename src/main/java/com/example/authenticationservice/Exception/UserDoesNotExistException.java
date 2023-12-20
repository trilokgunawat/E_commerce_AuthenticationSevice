package com.example.authenticationservice.Exception;

public class UserDoesNotExistException extends Exception{
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
