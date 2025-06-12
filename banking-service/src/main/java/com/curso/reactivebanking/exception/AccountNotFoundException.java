package com.curso.reactivebanking.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(UUID accountId) {
        super("Cuenta no encontrada con ID: " + accountId);
    }
    
    public AccountNotFoundException(String accountNumber) {
        super("Cuenta no encontrada con número: " + accountNumber);
    }
} 