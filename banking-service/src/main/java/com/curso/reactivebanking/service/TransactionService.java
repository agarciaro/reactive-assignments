package com.curso.reactivebanking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curso.reactivebanking.dto.TransactionDTO;
import com.curso.reactivebanking.dto.TransferRequestDTO;
import com.curso.reactivebanking.exception.AccountNotFoundException;
import com.curso.reactivebanking.exception.InsufficientFundsException;
import com.curso.reactivebanking.exception.TransactionNotFoundException;
import com.curso.reactivebanking.model.Transaction;
import com.curso.reactivebanking.model.TransactionStatus;
import com.curso.reactivebanking.repository.AccountRepository;
import com.curso.reactivebanking.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final FraudDetectionService fraudDetectionService;
    
    // Sink para streaming de transacciones en tiempo real
    private final Sinks.Many<TransactionDTO> transactionSink = 
        Sinks.many().multicast().onBackpressureBuffer();
    
    /**
     * Realiza una transferencia entre cuentas
     */
    @Transactional
    public Mono<TransactionDTO> transfer(TransferRequestDTO transferRequest) {
        log.info("Iniciando transferencia: {} -> {}, monto: {}", 
                transferRequest.getFromAccountId(), 
                transferRequest.getToAccountId(), 
                transferRequest.getAmount());
        
        // Validar que las cuentas no sean la misma
        if (Objects.equals(transferRequest.getFromAccountId(), transferRequest.getToAccountId())) {
            return Mono.error(new IllegalArgumentException("Las cuentas origen y destino no pueden ser la misma"));
        }
        
        return validateAccountsExist(transferRequest.getFromAccountId(), transferRequest.getToAccountId())
            .then(validateSufficientFunds(transferRequest.getFromAccountId(), transferRequest.getAmount()))
            .then(createTransaction(transferRequest))
            .flatMap(fraudDetectionService::analyzeTransaction)
            .flatMap(this::processTransaction)
            .map(this::mapToDTO)
            .doOnSuccess(this::publishTransaction)
            .doOnSuccess(dto -> log.info("Transferencia completada: {}, Estado: {}", 
                    dto.getId(), dto.getStatus()))
            .doOnError(error -> log.error("Error en transferencia: {}", error.getMessage()));
    }
    
    /**
     * Obtiene una transacción por ID
     */
    public Mono<TransactionDTO> getTransactionById(UUID transactionId) {
        log.debug("Buscando transacción: {}", transactionId);
        
        return transactionRepository.findById(transactionId)
            .switchIfEmpty(Mono.error(new TransactionNotFoundException(transactionId)))
            .map(this::mapToDTO)
            .doOnSuccess(dto -> log.debug("Transacción encontrada: {}", dto.getId()));
    }
    
    /**
     * Obtiene el historial de transacciones de una cuenta
     */
    public Flux<TransactionDTO> getAccountTransactions(UUID accountId) {
        log.debug("Obteniendo transacciones de cuenta: {}", accountId);
        
        return accountRepository.existsById(accountId)
            .flatMapMany(exists -> {
                if (!exists) {
                    return Flux.error(new AccountNotFoundException(accountId));
                }
                return transactionRepository.findByAccountId(accountId)
                    .map(this::mapToDTO);
            })
            .doOnComplete(() -> log.debug("Consulta de transacciones completada para cuenta: {}", accountId));
    }
    
    /**
     * Stream de transacciones en tiempo real
     */
    public Flux<TransactionDTO> getTransactionStream() {
        log.debug("Cliente conectado al stream de transacciones");
        return transactionSink.asFlux()
            .doOnCancel(() -> log.debug("Cliente desconectado del stream"));
    }
    
    /**
     * Obtiene las últimas transacciones
     */
    public Flux<TransactionDTO> getLatestTransactions(int limit) {
        log.debug("Obteniendo las últimas {} transacciones", limit);
        
        return transactionRepository.findLatestTransactions(limit)
            .map(this::mapToDTO)
            .doOnComplete(() -> log.debug("Consulta de últimas transacciones completada"));
    }
    
    /**
     * Valida que ambas cuentas existan
     */
    private Mono<Void> validateAccountsExist(UUID fromAccountId, UUID toAccountId) {
        return Mono.zip(
                accountRepository.existsById(fromAccountId),
                accountRepository.existsById(toAccountId)
            )
            .flatMap(tuple -> {
                boolean fromExists = tuple.getT1();
                boolean toExists = tuple.getT2();
                
                if (!fromExists) {
                    return Mono.error(new AccountNotFoundException(fromAccountId));
                }
                if (!toExists) {
                    return Mono.error(new AccountNotFoundException(toAccountId));
                }
                
                return Mono.empty();
            });
    }
    
    /**
     * Valida que la cuenta tenga fondos suficientes
     */
    private Mono<Void> validateSufficientFunds(UUID accountId, BigDecimal amount) {
        return accountRepository.hasSufficientFunds(accountId, amount)
            .flatMap(hasFunds -> {
                if (!hasFunds) {
                    return Mono.error(new InsufficientFundsException(accountId, amount));
                }
                return Mono.empty();
            });
    }
    
    /**
     * Crea una nueva transacción
     */
    private Mono<Transaction> createTransaction(TransferRequestDTO transferRequest) {
        Transaction transaction = Transaction.builder()
            .fromAccountId(transferRequest.getFromAccountId())
            .toAccountId(transferRequest.getToAccountId())
            .amount(transferRequest.getAmount())
            .description(transferRequest.getDescription())
            .timestamp(LocalDateTime.now())
            .status(TransactionStatus.PENDING)
            .build();
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Procesa la transacción según su estado después del análisis de fraude
     */
    private Mono<Transaction> processTransaction(Transaction transaction) {
        return switch (transaction.getStatus()) {
            case APPROVED -> executeTransfer(transaction);
            case PENDING -> {
                log.warn("Transacción {} requiere revisión manual", transaction.getId());
                yield transactionRepository.save(transaction);
            }
            case REJECTED -> {
                log.warn("Transacción {} rechazada por fraude", transaction.getId());
                yield transactionRepository.save(transaction);
            }
            default -> Mono.error(new IllegalStateException("Estado de transacción inválido"));
        };
    }
    
    /**
     * Ejecuta la transferencia actualizando los balances
     */
    private Mono<Transaction> executeTransfer(Transaction transaction) {
        log.debug("Ejecutando transferencia: {}", transaction.getId());
        
        return Mono.zip(
                // Debitar cuenta origen
                accountRepository.updateBalanceById(
                    transaction.getFromAccountId(), 
                    transaction.getAmount().negate()
                ),
                // Acreditar cuenta destino
                accountRepository.updateBalanceById(
                    transaction.getToAccountId(), 
                    transaction.getAmount()
                )
            )
            .flatMap(tuple -> {
                int debitResult = tuple.getT1();
                int creditResult = tuple.getT2();
                
                if (debitResult > 0 && creditResult > 0) {
                    transaction.setStatus(TransactionStatus.APPROVED);
                    log.info("Transferencia ejecutada exitosamente: {}", transaction.getId());
                } else {
                    transaction.setStatus(TransactionStatus.REJECTED);
                    transaction.setFraudAnalysis("Error actualizando balances");
                    log.error("Error ejecutando transferencia: {}", transaction.getId());
                }
                
                return transactionRepository.save(transaction);
            });
    }
    
    /**
     * Publica la transacción al stream en tiempo real
     */
    private void publishTransaction(TransactionDTO transactionDTO) {
        transactionSink.tryEmitNext(transactionDTO);
    }
    
    /**
     * Convierte Transaction a TransactionDTO
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
            .id(transaction.getId())
            .fromAccountId(transaction.getFromAccountId())
            .toAccountId(transaction.getToAccountId())
            .amount(transaction.getAmount())
            .timestamp(transaction.getTimestamp())
            .status(transaction.getStatus())
            .fraudAnalysis(transaction.getFraudAnalysis())
            .description(transaction.getDescription())
            .build();
    }
} 