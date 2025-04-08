package com.user_organization_management.exception;

public class BadCredentialsAuthException extends RuntimeException{

    public BadCredentialsAuthException(String message){
        super(message);
    }
}
