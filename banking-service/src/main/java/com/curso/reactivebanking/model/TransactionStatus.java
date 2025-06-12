package com.curso.reactivebanking.model;

/**
 * Enum que representa los posibles estados de una transacción
 */
public enum TransactionStatus {
    /**
     * Transacción pendiente de procesamiento
     */
    PENDING,
    
    /**
     * Transacción aprobada y completada
     */
    APPROVED,
    
    /**
     * Transacción rechazada (por fraude, fondos insuficientes, etc.)
     */
    REJECTED
} 