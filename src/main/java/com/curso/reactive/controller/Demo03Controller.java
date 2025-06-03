package com.curso.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/demo03")
@Tag(name = "demo03")
public class Demo03Controller {

    private final Random random = new Random();

    @GetMapping("/product/{id}")
    @Operation(
            summary = "Product Service",
            description = "Provides the product name for the given product id (1,2,3,4)\n",
            operationId = "getProduct_1"
    )
    public Mono<String> getProduct(@PathVariable int id) {
        if (id < 1 || id > 4) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 4"));
        }
        
        // Simulate random delays and potential timeouts
        Duration delay = Duration.ofMillis(100 + random.nextInt(2000));
        
        return Mono.just("Product " + id)
                .delayElement(delay)
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(throwable -> Mono.just("Fallback Product " + id));
    }

    @GetMapping("/timeout-fallback/product/{id}")
    @Operation(
            summary = "Fallback Service for Timeout",
            description = "Provides the product name for the given product id (1,2,3,4)\n",
            operationId = "getTimeoutFallback"
    )
    public Mono<String> getTimeoutFallback(@PathVariable int id) {
        if (id < 1 || id > 4) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 4"));
        }
        
        // Simulate timeout scenario - always delay more than 1 second
        return Mono.just("Product " + id)
                .delayElement(Duration.ofSeconds(3))
                .timeout(Duration.ofSeconds(1))
                .onErrorReturn("Timeout Fallback Product " + id);
    }

    @GetMapping("/empty-fallback/product/{id}")
    @Operation(
            summary = "Fallback Service for Empty",
            description = "Provides the product name for the given product id (1,2,3,4)\n",
            operationId = "getEmptyFallback"
    )
    public Mono<String> getEmptyFallback(@PathVariable int id) {
        if (id < 1 || id > 4) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 4"));
        }
        
        // Simulate empty response scenario
        if (random.nextBoolean()) {
            return Mono.<String>empty()
                    .switchIfEmpty(Mono.just("Empty Fallback Product " + id));
        }
        
        return Mono.just("Product " + id);
    }
} 