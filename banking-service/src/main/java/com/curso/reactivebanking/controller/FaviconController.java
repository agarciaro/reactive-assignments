package com.curso.reactivebanking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FaviconController {
	
	@GetMapping("/favicon.ico")
    public Mono<Void> favicon() {
        return Mono.empty(); // Responde con 204 No Content
    }
}
