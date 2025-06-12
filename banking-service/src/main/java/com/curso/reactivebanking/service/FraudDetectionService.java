package com.curso.reactivebanking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.curso.reactivebanking.model.Transaction;
import com.curso.reactivebanking.model.TransactionStatus;
import com.curso.reactivebanking.repository.TransactionRepository;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    
    private final TransactionRepository transactionRepository;
    
    @Value("${banking.fraud.high-amount-threshold:5000.00}")
    private BigDecimal highAmountThreshold;
    
    @Value("${banking.fraud.max-transactions-per-minute:3}")
    private int maxTransactionsPerMinute;
    
    @Value("${banking.fraud.suspicious-hours.start:22}")
    private int suspiciousHoursStart;
    
    @Value("${banking.fraud.suspicious-hours.end:6}")
    private int suspiciousHoursEnd;
    
    /**
     * Analiza una transacción para detectar fraude
     */
    public Mono<Transaction> analyzeTransaction(Transaction transaction) {
        log.debug("Analizando transacción para fraude: {}", transaction.getId());
        
        return Mono.just(transaction)
            .flatMap(this::checkHighAmountRule)
            .flatMap(this::checkFrequencyRule)
            .flatMap(this::checkSuspiciousHoursRule)
            .doOnNext(t -> log.info("Análisis de fraude completado para transacción {}: Estado={}, Análisis={}", 
                    t.getId(), t.getStatus(), t.getFraudAnalysis()));
    }
    
    /**
     * Regla: Transacciones de alto monto son sospechosas
     */
    private Mono<Transaction> checkHighAmountRule(Transaction transaction) {
        if (transaction.getAmount().compareTo(highAmountThreshold) > 0) {
            transaction.setStatus(TransactionStatus.PENDING);
            addToFraudAnalysis(transaction, "Alto monto: " + transaction.getAmount());
            log.warn("Transacción {} marcada como sospechosa por alto monto: {}", 
                    transaction.getId(), transaction.getAmount());
        }
        return Mono.just(transaction);
    }
    
    /**
     * Regla: Múltiples transacciones en poco tiempo son sospechosas
     */
    private Mono<Transaction> checkFrequencyRule(Transaction transaction) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        
        return transactionRepository.countTransactionsFromAccountSince(
                transaction.getFromAccountId(), oneMinuteAgo)
            .map(count -> {
                if (count >= maxTransactionsPerMinute) {
                    transaction.setStatus(TransactionStatus.PENDING);
                    addToFraudAnalysis(transaction, 
                        String.format("Frecuencia alta: %d transacciones en 1 minuto", count));
                    log.warn("Transacción {} marcada como sospechosa por alta frecuencia: {} transacciones", 
                            transaction.getId(), count);
                }
                return transaction;
            });
    }
    
    /**
     * Regla: Transacciones en horarios inusuales son sospechosas
     */
    private Mono<Transaction> checkSuspiciousHoursRule(Transaction transaction) {
        int hour = transaction.getTimestamp().getHour();
        
        // Horario sospechoso: 22:00 - 06:00
        boolean isSuspiciousHour = hour >= suspiciousHoursStart || hour <= suspiciousHoursEnd;
        
        if (isSuspiciousHour) {
            // Solo marcar como pendiente si no está ya rechazada
            if (transaction.getStatus() != TransactionStatus.REJECTED) {
                transaction.setStatus(TransactionStatus.PENDING);
            }
            addToFraudAnalysis(transaction, "Horario inusual: " + hour + ":00");
            log.warn("Transacción {} marcada como sospechosa por horario inusual: {}:00", 
                    transaction.getId(), hour);
        }
        
        // Si no se detectó fraude, aprobar la transacción
        if (transaction.getStatus() == null || transaction.getStatus() == TransactionStatus.PENDING) {
            if (transaction.getFraudAnalysis() == null || transaction.getFraudAnalysis().trim().isEmpty()) {
                transaction.setStatus(TransactionStatus.APPROVED);
                transaction.setFraudAnalysis("Sin indicadores de fraude");
            }
        }
        
        return Mono.just(transaction);
    }
    
    /**
     * Agrega información al análisis de fraude
     */
    private void addToFraudAnalysis(Transaction transaction, String analysis) {
        String currentAnalysis = transaction.getFraudAnalysis();
        if (currentAnalysis == null || currentAnalysis.trim().isEmpty()) {
            transaction.setFraudAnalysis(analysis);
        } else {
            transaction.setFraudAnalysis(currentAnalysis + "; " + analysis);
        }
    }
    
    /**
     * Obtiene todas las transacciones sospechosas
     */
    public Mono<List<Transaction>> getSuspiciousTransactions() {
        return transactionRepository.findSuspiciousTransactions()
            .collectList()
            .doOnNext(transactions -> log.info("Encontradas {} transacciones sospechosas", transactions.size()));
    }
} 