package com.curso.reactivebanking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.curso.reactivebanking.dto.AccountDTO;
import com.curso.reactivebanking.exception.AccountNotFoundException;
import com.curso.reactivebanking.exception.DuplicateAccountException;
import com.curso.reactivebanking.model.Account;
import com.curso.reactivebanking.repository.AccountRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    /**
     * Crea una nueva cuenta
     */
    public Mono<AccountDTO> createAccount(AccountDTO accountDTO) {
        log.info("Creando nueva cuenta: {}", accountDTO.getAccountNumber());
        
        return accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new DuplicateAccountException(accountDTO.getAccountNumber()));
                }
                
                Account account = Account.builder()
                    .accountNumber(accountDTO.getAccountNumber())
                    .ownerName(accountDTO.getOwnerName())
                    .balance(accountDTO.getBalance())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                
                return accountRepository.save(account);
            })
            .map(this::mapToDTO)
            .doOnSuccess(dto -> log.info("Cuenta creada exitosamente: {}", dto.getAccountNumber()))
            .doOnError(error -> log.error("Error creando cuenta: {}", error.getMessage()));
    }
    
    /**
     * Obtiene una cuenta por ID
     */
    public Mono<AccountDTO> getAccountById(UUID accountId) {
        log.debug("Buscando cuenta por ID: {}", accountId);
        
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
            .map(this::mapToDTO)
            .doOnSuccess(dto -> log.debug("Cuenta encontrada: {}", dto.getAccountNumber()));
    }
    
    /**
     * Obtiene una cuenta por número de cuenta
     */
    public Mono<AccountDTO> getAccountByNumber(String accountNumber) {
        log.debug("Buscando cuenta por número: {}", accountNumber);
        
        return accountRepository.findByAccountNumber(accountNumber)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)))
            .map(this::mapToDTO)
            .doOnSuccess(dto -> log.debug("Cuenta encontrada: {}", dto.getAccountNumber()));
    }
    
    /**
     * Obtiene todas las cuentas
     */
    public Flux<AccountDTO> getAllAccounts() {
        log.debug("Obteniendo todas las cuentas");
        
        return accountRepository.findAll()
            .map(this::mapToDTO)
            .doOnComplete(() -> log.debug("Consulta de todas las cuentas completada"));
    }
    
    /**
     * Actualiza una cuenta
     */
    public Mono<AccountDTO> updateAccount(UUID accountId, AccountDTO accountDTO) {
        log.info("Actualizando cuenta: {}", accountId);
        
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
            .flatMap(existingAccount -> {
                // Verificar si el nuevo número de cuenta ya existe (si cambió)
                if (!existingAccount.getAccountNumber().equals(accountDTO.getAccountNumber())) {
                    return accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new DuplicateAccountException(accountDTO.getAccountNumber()));
                            }
                            return updateAccountFields(existingAccount, accountDTO);
                        });
                } else {
                    return updateAccountFields(existingAccount, accountDTO);
                }
            })
            .flatMap(accountRepository::save)
            .map(this::mapToDTO)
            .doOnSuccess(dto -> log.info("Cuenta actualizada exitosamente: {}", dto.getAccountNumber()))
            .doOnError(error -> log.error("Error actualizando cuenta: {}", error.getMessage()));
    }
    
    /**
     * Obtiene el balance de una cuenta
     */
    public Mono<BigDecimal> getAccountBalance(UUID accountId) {
        log.debug("Obteniendo balance de cuenta: {}", accountId);
        
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
            .map(Account::getBalance)
            .doOnSuccess(balance -> log.debug("Balance obtenido: {}", balance));
    }
    
    /**
     * Verifica si una cuenta existe
     */
    public Mono<Boolean> accountExists(UUID accountId) {
        return accountRepository.existsById(accountId);
    }
    
    /**
     * Verifica si una cuenta tiene fondos suficientes
     */
    public Mono<Boolean> hasSufficientFunds(UUID accountId, BigDecimal amount) {
        return accountRepository.hasSufficientFunds(accountId, amount);
    }
    
    /**
     * Actualiza los campos de una cuenta
     */
    private Mono<Account> updateAccountFields(Account existingAccount, AccountDTO accountDTO) {
        existingAccount.setAccountNumber(accountDTO.getAccountNumber());
        existingAccount.setOwnerName(accountDTO.getOwnerName());
        existingAccount.setBalance(accountDTO.getBalance());
        existingAccount.setUpdatedAt(LocalDateTime.now());
        return Mono.just(existingAccount);
    }
    
    /**
     * Convierte Account a AccountDTO
     */
    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
            .id(account.getId())
            .accountNumber(account.getAccountNumber())
            .ownerName(account.getOwnerName())
            .balance(account.getBalance())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }
} 