package com.curso.reactivebanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    
    private UUID id;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String accountNumber;
    
    @NotBlank(message = "El nombre del propietario es obligatorio")
    private String ownerName;
    
    @NotNull(message = "El balance es obligatorio")
    @PositiveOrZero(message = "El balance debe ser positivo o cero")
    private BigDecimal balance;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 