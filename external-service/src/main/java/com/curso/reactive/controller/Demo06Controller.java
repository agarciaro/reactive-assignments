package com.curso.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/demo06")
@Tag(name = "demo06")
public class Demo06Controller {

    private static final String[] COUNTRIES = {
            "USA", "Canada", "Mexico", "Brazil", "Argentina", "UK", "France", "Germany", 
            "Italy", "Spain", "Japan", "China", "India", "Australia", "Russia", "South Africa"
    };
    
    private final Random random = new Random();

    @GetMapping("/country")
    @Operation(
            summary = "Country Name Service",
            description = "Provides the random country name. response time 100ms\n",
            operationId = "getCountry"
    )
    public Mono<String> getCountry() {
        return Mono.just(COUNTRIES[random.nextInt(COUNTRIES.length)])
                .delayElement(Duration.ofMillis(100));
    }

    @GetMapping("/product/{id}")
    @Operation(
            summary = "Product Service",
            description = "Provides the product name for the given product id.\n" +
                    "1 - bad request.\n" +
                    "2 - random error.\n",
            operationId = "getProduct"
    )
    public Mono<String> getProduct(@PathVariable int id) {
        // ID 1 always returns bad request
        if (id == 1) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request for product ID 1"));
        }
        
        // ID 2 has random error simulation
        if (id == 2) {
            if (random.nextBoolean()) {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Random error for product ID 2"));
            }
        }
        
        // For other IDs, return normal product
        return Mono.just("Product " + id);
    }
} 