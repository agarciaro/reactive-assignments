package com.curso.reactivebanking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    
    @NotNull(message = "La cuenta origen es obligatoria")
    private UUID fromAccountId;
    
    @NotNull(message = "La cuenta destino es obligatoria")
    private UUID toAccountId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
    
    private String description;
} 