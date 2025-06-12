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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.curso.reactivebanking.dto.AccountDTO;
import com.curso.reactivebanking.service.AccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Gestión de cuentas bancarias")
public class AccountController {
    
    private final AccountService accountService;
    
    @Operation(summary = "Crear nueva cuenta", description = "Crea una nueva cuenta bancaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Número de cuenta ya existe")
    })
    @PostMapping
    public Mono<ResponseEntity<AccountDTO>> createAccount(
            @Valid @RequestBody AccountDTO accountDTO) {
        
        log.info("POST /api/accounts - Crear cuenta: {}", accountDTO.getAccountNumber());
        
        return accountService.createAccount(accountDTO)
            .map(account -> ResponseEntity.status(HttpStatus.CREATED).body(account))
            .doOnSuccess(response -> log.info("Cuenta creada exitosamente: {}", 
                    response.getBody().getAccountNumber()));
    }
    
    @Operation(summary = "Obtener cuenta por ID", description = "Obtiene los detalles de una cuenta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountDTO>> getAccountById(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id) {
        
        log.debug("GET /api/accounts/{} - Obtener cuenta", id);
        
        return accountService.getAccountById(id)
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.debug("Cuenta obtenida: {}", 
                    response.getBody().getAccountNumber()));
    }
    
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene todas las cuentas del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas")
    @GetMapping
    public Flux<AccountDTO> getAllAccounts() {
        log.debug("GET /api/accounts - Obtener todas las cuentas");
        
        return accountService.getAllAccounts()
            .doOnComplete(() -> log.debug("Consulta de todas las cuentas completada"));
    }
    
    @Operation(summary = "Actualizar cuenta", description = "Actualiza los datos de una cuenta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Número de cuenta duplicado")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountDTO>> updateAccount(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id,
            @Valid @RequestBody AccountDTO accountDTO) {
        
        log.info("PUT /api/accounts/{} - Actualizar cuenta", id);
        
        return accountService.updateAccount(id, accountDTO)
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.info("Cuenta actualizada: {}", 
                    response.getBody().getAccountNumber()));
    }
    
    @Operation(summary = "Obtener balance de cuenta", description = "Consulta el balance actual de una cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance obtenido"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}/balance")
    public Mono<ResponseEntity<BigDecimal>> getAccountBalance(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id) {
        
        log.debug("GET /api/accounts/{}/balance - Obtener balance", id);
        
        return accountService.getAccountBalance(id)
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.debug("Balance obtenido: {}", response.getBody()));
    }
} 