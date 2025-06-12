package com.curso.reactivebanking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.curso.reactivebanking.model.Transaction;
import com.curso.reactivebanking.service.FraudDetectionService;
import com.curso.reactivebanking.service.TransactionService;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Tag(name = "Detección de Fraude", description = "Análisis y detección de transacciones fraudulentas")
public class FraudController {
    
    private final FraudDetectionService fraudDetectionService;
    private final TransactionService transactionService;
    
    @Operation(summary = "Analizar transacción", 
               description = "Analiza una transacción específica para detectar fraude")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Análisis completado"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/analyze/{transactionId}")
    public Mono<ResponseEntity<String>> analyzeTransaction(
            @Parameter(description = "ID de la transacción a analizar") 
            @PathVariable UUID transactionId) {
        
        log.info("GET /api/fraud/analyze/{} - Analizar transacción", transactionId);
        
        return transactionService.getTransactionById(transactionId)
            .map(transactionDTO -> {
                // Convertir DTO a entidad para análisis
                Transaction transaction = Transaction.builder()
                    .id(transactionDTO.getId())
                    .fromAccountId(transactionDTO.getFromAccountId())
                    .toAccountId(transactionDTO.getToAccountId())
                    .amount(transactionDTO.getAmount())
                    .timestamp(transactionDTO.getTimestamp())
                    .status(transactionDTO.getStatus())
                    .fraudAnalysis(transactionDTO.getFraudAnalysis())
                    .description(transactionDTO.getDescription())
                    .build();
                return transaction;
            })
            .flatMap(fraudDetectionService::analyzeTransaction)
            .map(analyzedTransaction -> {
                String result = String.format(
                    "Análisis de fraude para transacción %s:\n" +
                    "Estado: %s\n" +
                    "Análisis: %s\n" +
                    "Monto: %s\n" +
                    "Fecha: %s",
                    analyzedTransaction.getId(),
                    analyzedTransaction.getStatus(),
                    analyzedTransaction.getFraudAnalysis() != null ? 
                        analyzedTransaction.getFraudAnalysis() : "Sin análisis",
                    analyzedTransaction.getAmount(),
                    analyzedTransaction.getTimestamp()
                );
                return ResponseEntity.ok(result);
            })
            .doOnSuccess(response -> log.info("Análisis de fraude completado para transacción: {}", transactionId));
    }
    
    @Operation(summary = "Transacciones sospechosas", 
               description = "Obtiene todas las transacciones marcadas como sospechosas")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones sospechosas")
    @GetMapping("/suspicious")
    public Mono<ResponseEntity<List<Transaction>>> getSuspiciousTransactions() {
        log.info("GET /api/fraud/suspicious - Obtener transacciones sospechosas");
        
        return fraudDetectionService.getSuspiciousTransactions()
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.info("Encontradas {} transacciones sospechosas", 
                    response.getBody().size()));
    }
} 