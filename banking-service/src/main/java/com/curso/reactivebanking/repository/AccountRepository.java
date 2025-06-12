package com.curso.reactivebanking.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.curso.reactivebanking.model.Account;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, UUID> {
    
    /**
     * Busca una cuenta por su número de cuenta
     */
    Mono<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Verifica si existe una cuenta con el número especificado
     */
    Mono<Boolean> existsByAccountNumber(String accountNumber);
    
    /**
     * Actualiza el balance de una cuenta de forma atómica
     */
    @Modifying
    @Query("UPDATE accounts SET balance = balance + :amount, updated_at = CURRENT_TIMESTAMP WHERE id = :accountId")
    Mono<Integer> updateBalanceById(UUID accountId, BigDecimal amount);
    
    /**
     * Verifica si una cuenta tiene fondos suficientes
     */
    @Query("SELECT CASE WHEN balance >= :amount THEN true ELSE false END FROM accounts WHERE id = :accountId")
    Mono<Boolean> hasSufficientFunds(UUID accountId, BigDecimal amount);
} 