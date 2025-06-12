package com.curso.reactivebanking.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction {
    
    @Id
    private UUID id;
    
    @NotNull(message = "La cuenta origen no puede ser nula")
    private UUID fromAccountId;
    
    @NotNull(message = "La cuenta destino no puede ser nula")
    private UUID toAccountId;
    
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
    
    private LocalDateTime timestamp;
    
    @NotNull(message = "El estado no puede ser nulo")
    private TransactionStatus status;
    
    private String fraudAnalysis;
    
    private String description;
    
    public Transaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
} 