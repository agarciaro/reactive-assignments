package com.curso.reactivebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableR2dbcAuditing
@Slf4j
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("""
            
            🏦 Sistema Bancario Reactivo - Iniciado correctamente
            
            📚 Documentación API: http://localhost:8080/swagger-ui.html
            🔍 H2 Console: http://localhost:8080/h2-console
            📊 Health Check: http://localhost:8080/actuator/health
            🌐 Endpoints principales:
               • GET    /api/accounts                    - Listar cuentas
               • POST   /api/accounts                    - Crear cuenta
               • POST   /api/transactions/transfer       - Realizar transferencia
               • GET    /api/transactions/stream         - Stream tiempo real
               • GET    /api/fraud/suspicious            - Transacciones sospechosas
            
            """);
    }
} 