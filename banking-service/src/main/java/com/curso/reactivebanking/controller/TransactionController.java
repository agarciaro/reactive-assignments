package com.curso.reactivebanking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.curso.reactivebanking.dto.TransactionDTO;
import com.curso.reactivebanking.dto.TransferRequestDTO;
import com.curso.reactivebanking.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Gestión de transacciones bancarias")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Operation(summary = "Realizar transferencia")
    @PostMapping("/transfer")
    public Mono<ResponseEntity<TransactionDTO>> transfer(@Valid @RequestBody TransferRequestDTO transferRequest) {
        log.info("POST /api/transactions/transfer - Transferir: {} -> {}, monto: {}", 
                transferRequest.getFromAccountId(), 
                transferRequest.getToAccountId(), 
                transferRequest.getAmount());
        
        return transactionService.transfer(transferRequest)
            .map(transaction -> ResponseEntity.status(HttpStatus.CREATED).body(transaction));
    }
    
    @Operation(summary = "Obtener transacción por ID")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionDTO>> getTransactionById(@PathVariable UUID id) {
        log.debug("GET /api/transactions/{} - Obtener transacción", id);
        
        return transactionService.getTransactionById(id)
            .map(ResponseEntity::ok);
    }
    
    @Operation(summary = "Historial de transacciones de cuenta")
    @GetMapping("/account/{accountId}")
    public Flux<TransactionDTO> getAccountTransactions(@PathVariable UUID accountId) {
        log.debug("GET /api/transactions/account/{} - Historial de transacciones", accountId);
        
        return transactionService.getAccountTransactions(accountId);
    }
    
    @Operation(summary = "Stream de transacciones en tiempo real")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TransactionDTO> getTransactionStream() {
        log.info("GET /api/transactions/stream - Cliente conectado al stream");
        
        return transactionService.getTransactionStream();
    }
    
    @Operation(summary = "Últimas transacciones")
    @GetMapping("/latest")
    public Flux<TransactionDTO> getLatestTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/transactions/latest?limit={} - Últimas transacciones", limit);
        
        return transactionService.getLatestTransactions(limit);
    }
} 