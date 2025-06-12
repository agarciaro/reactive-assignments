package com.curso.reactivebanking.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(UUID accountId, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Fondos insuficientes en cuenta %s. Solicitado: %s, Disponible: %s", 
                          accountId, requestedAmount, availableBalance));
    }
    
    public InsufficientFundsException(UUID accountId, BigDecimal requestedAmount) {
        super(String.format("Fondos insuficientes en cuenta %s para el monto solicitado: %s", 
                          accountId, requestedAmount));
    }
} 