package com.curso.reactivebanking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.curso.reactivebanking.model.TransactionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    private UUID id;
    
    @NotNull(message = "La cuenta origen es obligatoria")
    private UUID fromAccountId;
    
    @NotNull(message = "La cuenta destino es obligatoria")
    private UUID toAccountId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
    
    private LocalDateTime timestamp;
    
    private TransactionStatus status;
    
    private String fraudAnalysis;
    
    private String description;
} 