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
@RequestMapping("/demo02")
@Tag(name = "demo02")
public class Demo02Controller {

    private static final String[] NAMES = {
            "Alice", "Bob", "Charlie", "Diana", "Emma", "Frank", "Grace", "Henry", 
            "Isabella", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Peter"
    };
    
    private final Random random = new Random();

    @GetMapping(value = "/name/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Streaming Service",
            description = "Generates random first names every 500 ms!\n",
            operationId = "nameStream"
    )
    public Flux<String> nameStream() {
        return Flux.interval(Duration.ofMillis(500))
                .map(i -> NAMES[random.nextInt(NAMES.length)])
                .take(40); // Stream for 20 seconds (40 * 500ms)
    }

    @GetMapping(value = "/stock/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Assignment: Stock Service",
            description = "Sends stock price to the observer periodically!\n" +
                    "The stock price can be between 80 - 120. This service will emit price changes every 500ms for ~20 seconds.\n",
            operationId = "stockStream"
    )
    public Flux<String> stockStream() {
        return Flux.interval(Duration.ofMillis(500))
                .map(i -> {
                    int price = 80 + random.nextInt(41); // Random between 80-120
                    return String.valueOf(price); // Retorna solo el precio como string
                })
                .take(40) // Stream for 20 seconds (40 * 500ms)
                .doOnNext(price -> System.out.println("Emitting stock price: " + price)); // Log para debugging
    }
} 