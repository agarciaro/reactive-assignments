package com.curso.reactivebanking.exception;

public class DuplicateAccountException extends RuntimeException {
    
    public DuplicateAccountException(String accountNumber) {
        super("Ya existe una cuenta con el número: " + accountNumber);
    }
} 