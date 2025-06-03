package com.curso.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@RequestMapping("/demo07")
@Tag(name = "demo07")
public class Demo07Controller {

    private static final String[] BOOKS = {
            "The Great Gatsby", "To Kill a Mockingbird", "1984", "Pride and Prejudice",
            "The Catcher in the Rye", "Lord of the Flies", "The Chronicles of Narnia",
            "Harry Potter and the Sorcerer's Stone", "The Hobbit", "Fahrenheit 451",
            "Jane Eyre", "Wuthering Heights", "The Picture of Dorian Gray",
            "Brave New World", "The Lord of the Rings", "Gone with the Wind"
    };
    
    private final Random random = new Random();

    @GetMapping("/book")
    @Operation(
            summary = "Book Service",
            description = "Gives a random book name\n",
            operationId = "getBook"
    )
    public Mono<String> getBook() {
        return Mono.just(BOOKS[random.nextInt(BOOKS.length)]);
    }
} 