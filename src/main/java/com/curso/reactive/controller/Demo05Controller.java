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
@RequestMapping("/demo05")
@Tag(name = "demo05")
public class Demo05Controller {

    private static final String[] PRODUCT_NAMES = {
            "Laptop", "Phone", "Tablet", "Headphones", "Watch", 
            "Camera", "Keyboard", "Mouse", "Monitor", "Speaker"
    };
    
    private static final String[] REVIEW_COMMENTS = {
            "Excellent product!", "Good value for money", "Could be better", 
            "Amazing quality", "Not satisfied", "Highly recommended",
            "Average product", "Great customer service", "Fast delivery", "Perfect!"
    };
    
    private final Random random = new Random();

    @GetMapping("/product/{id}")
    @Operation(
            summary = "Product Name Service",
            description = "Gives the product name for product ids 1 - 10. Takes 1 second to respond.\n",
            operationId = "product"
    )
    public Mono<String> product(@PathVariable int id) {
        if (id < 1 || id > 10) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 10"));
        }
        
        return Mono.just(PRODUCT_NAMES[id - 1])
                .delayElement(Duration.ofSeconds(1));
    }

    @GetMapping("/price/{id}")
    @Operation(
            summary = "Price Service",
            description = "Gives the price for product ids 1 - 10. Takes 1 second to respond.\n",
            operationId = "price"
    )
    public Mono<String> price(@PathVariable int id) {
        if (id < 1 || id > 10) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 10"));
        }
        
        double price = 99.99 + (id * 50) + random.nextInt(100);
        return Mono.just("$" + String.format("%.2f", price))
                .delayElement(Duration.ofSeconds(1));
    }

    @GetMapping("/review/{id}")
    @Operation(
            summary = "Review Service",
            description = "Gives the review for product ids 1 - 10. Takes 1 second to respond.\n",
            operationId = "review"
    )
    public Mono<String> review(@PathVariable int id) {
        if (id < 1 || id > 10) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 10"));
        }
        
        int rating = 1 + random.nextInt(5);
        String comment = REVIEW_COMMENTS[random.nextInt(REVIEW_COMMENTS.length)];
        return Mono.just(rating + "/5 stars - " + comment)
                .delayElement(Duration.ofSeconds(1));
    }
} 