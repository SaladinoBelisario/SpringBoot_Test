package com.example.springboot_test.exceptions;

public class DineroInsificienteException extends RuntimeException{
    public DineroInsificienteException(String message) {
        super(message);
    }
}
