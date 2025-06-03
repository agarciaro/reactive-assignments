package com.curso.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/demo04")
@Tag(name = "demo04")
public class Demo04Controller {

    private static final String[] PRODUCTS = {
            "Laptop", "Phone", "Tablet", "Headphones", "Watch", "Camera", "Keyboard", "Mouse"
    };
    
    private final Random random = new Random();

    @GetMapping(value = "/orders/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Order Service",
            description = "Order stream\n",
            operationId = "orderStream"
    )
    public Flux<String> orderStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> {
                    String product = PRODUCTS[random.nextInt(PRODUCTS.length)];
                    int quantity = 1 + random.nextInt(5);
                    int orderId = 1000 + random.nextInt(9000);
                    return "Order " + orderId + ": " + quantity + "x " + product;
                })
                .take(30); // Stream for 30 seconds
    }
} 