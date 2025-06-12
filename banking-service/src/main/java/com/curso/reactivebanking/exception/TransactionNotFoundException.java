package com.curso.reactivebanking.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    
    public TransactionNotFoundException(UUID transactionId) {
        super("Transacción no encontrada con ID: " + transactionId);
    }
} 