package com.curso.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo01")
@Tag(name = "demo01")
public class Demo01Controller {

    @GetMapping("/product/{id}")
    @Operation(
            summary = "Product Service",
            description = "Provides the product name for the given product id (up to product id 100)\n",
            operationId = "getProduct_2"
    )
    public Mono<String> getProduct(@PathVariable int id) {
        if (id <= 0 || id > 1000) {
            return Mono.error(new IllegalArgumentException("Product ID must be between 1 and 100"));
        }
        
        return Mono.just("Product " + id);
    }
} 