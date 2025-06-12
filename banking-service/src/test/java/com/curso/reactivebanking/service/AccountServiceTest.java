package com.curso.reactivebanking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.curso.reactivebanking.dto.AccountDTO;
import com.curso.reactivebanking.exception.AccountNotFoundException;
import com.curso.reactivebanking.exception.DuplicateAccountException;
import com.curso.reactivebanking.model.Account;
import com.curso.reactivebanking.repository.AccountRepository;
import com.curso.reactivebanking.service.AccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @InjectMocks
    private AccountService accountService;
    
    private Account testAccount;
    private AccountDTO testAccountDTO;
    private UUID testAccountId;
    
    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testAccount = Account.builder()
            .id(testAccountId)
            .accountNumber("TEST001")
            .ownerName("Juan Pérez")
            .balance(BigDecimal.valueOf(1000))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        testAccountDTO = AccountDTO.builder()
            .accountNumber("TEST001")
            .ownerName("Juan Pérez")
            .balance(BigDecimal.valueOf(1000))
            .build();
    }
    
    @Test
    void createAccount_Success() {
        // Given
        when(accountRepository.existsByAccountNumber("TEST001"))
            .thenReturn(Mono.just(false));
        when(accountRepository.save(any(Account.class)))
            .thenReturn(Mono.just(testAccount));
        
        // When & Then
        StepVerifier.create(accountService.createAccount(testAccountDTO))
            .expectNextMatches(account -> 
                account.getAccountNumber().equals("TEST001") &&
                account.getOwnerName().equals("Juan Pérez") &&
                account.getBalance().equals(BigDecimal.valueOf(1000)))
            .verifyComplete();
    }
    
    @Test
    void createAccount_DuplicateAccount() {
        // Given
        when(accountRepository.existsByAccountNumber("TEST001"))
            .thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(accountService.createAccount(testAccountDTO))
            .expectError(DuplicateAccountException.class)
            .verify();
    }
    
    @Test
    void getAccountById_Success() {
        // Given
        when(accountRepository.findById(testAccountId))
            .thenReturn(Mono.just(testAccount));
        
        // When & Then
        StepVerifier.create(accountService.getAccountById(testAccountId))
            .expectNextMatches(account -> 
                account.getId().equals(testAccountId) &&
                account.getAccountNumber().equals("TEST001"))
            .verifyComplete();
    }
    
    @Test
    void getAccountById_NotFound() {
        // Given
        when(accountRepository.findById(testAccountId))
            .thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(accountService.getAccountById(testAccountId))
            .expectError(AccountNotFoundException.class)
            .verify();
    }
    
    @Test
    void getAllAccounts_Success() {
        // Given
        Account account2 = Account.builder()
            .id(UUID.randomUUID())
            .accountNumber("TEST002")
            .ownerName("María García")
            .balance(BigDecimal.valueOf(2000))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(accountRepository.findAll())
            .thenReturn(Flux.just(testAccount, account2));
        
        // When & Then
        StepVerifier.create(accountService.getAllAccounts())
            .expectNextCount(2)
            .verifyComplete();
    }
} 