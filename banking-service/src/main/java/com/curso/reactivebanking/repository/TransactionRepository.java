package com.curso.reactivebanking.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.curso.reactivebanking.model.Transaction;
import com.curso.reactivebanking.model.TransactionStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    
    /**
     * Obtiene todas las transacciones de una cuenta (enviadas o recibidas)
     */
    @Query("SELECT * FROM transactions WHERE from_account_id = :accountId OR to_account_id = :accountId ORDER BY timestamp DESC")
    Flux<Transaction> findByAccountId(UUID accountId);
    
    /**
     * Obtiene transacciones por estado
     */
    Flux<Transaction> findByStatus(TransactionStatus status);
    
    /**
     * Cuenta transacciones de una cuenta en un período de tiempo
     */
    @Query("SELECT COUNT(*) FROM transactions WHERE from_account_id = :accountId AND timestamp >= :fromTime")
    Mono<Long> countTransactionsFromAccountSince(UUID accountId, LocalDateTime fromTime);
    
    /**
     * Obtiene transacciones sospechosas (pendientes o rechazadas)
     */
    @Query("SELECT * FROM transactions WHERE status IN ('PENDING', 'REJECTED') ORDER BY timestamp DESC")
    Flux<Transaction> findSuspiciousTransactions();
    
    /**
     * Obtiene las últimas transacciones ordenadas por fecha
     */
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    Flux<Transaction> findLatestTransactions(int limit);
} 