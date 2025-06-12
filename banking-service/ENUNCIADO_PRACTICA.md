# Práctica: Sistema Bancario Reactivo con Spring WebFlux y R2DBC

## Descripción General
Desarrolla un sistema bancario completo utilizando **Spring Boot 3**, **WebFlux**, **R2DBC** y patrones reactivos. El sistema debe manejar cuentas bancarias, transacciones y detección de fraude de manera completamente asíncrona y no bloqueante.

## Objetivos de Aprendizaje
- Implementar APIs REST reactivas con Spring WebFlux
- Utilizar R2DBC para operaciones de base de datos no bloqueantes
- Aplicar patrones reactivos con Reactor (Mono y Flux)
- Implementar manejo de errores reactivo
- Crear servicios de detección de fraude en tiempo real
- Aplicar validaciones y transformaciones reactivas

## Requerimientos Técnicos

### Stack Tecnológico
- **Spring Boot 3.2+**
- **Spring WebFlux** (API REST Reactiva)
- **Spring Data R2DBC** (Base de datos reactiva)
- **H2 Database** (En memoria para desarrollo)
- **Reactor Core** (Programación reactiva)
- **Bean Validation** (Validaciones)
- **Maven** (Gestión de dependencias)

### Arquitectura del Sistema
```
Controller Layer (WebFlux)
    ↓
Service Layer (Reactive Services)
    ↓
Repository Layer (R2DBC)
    ↓
Database (H2)
```

## Funcionalidades a Implementar

### 1. Gestión de Cuentas
- **Crear cuenta**: Endpoint POST `/api/accounts`
- **Consultar cuenta**: Endpoint GET `/api/accounts/{id}`
- **Listar todas las cuentas**: Endpoint GET `/api/accounts`
- **Consultar balance**: Endpoint GET `/api/accounts/{id}/balance`
- **Actualizar información**: Endpoint PUT `/api/accounts/{id}`

### 2. Sistema de Transacciones
- **Transferir dinero**: Endpoint POST `/api/transactions/transfer`
- **Consultar transacción**: Endpoint GET `/api/transactions/{id}`
- **Historial de transacciones**: Endpoint GET `/api/accounts/{accountId}/transactions`
- **Transacciones en tiempo real**: Endpoint GET `/api/transactions/stream` (Server-Sent Events)

### 3. Detección de Fraude
- **Análisis automático** de cada transacción
- **Reglas de detección**:
  - Transacciones > $5,000 (sospechosas)
  - Más de 3 transacciones en 1 minuto (patrón inusual)
  - Transacciones fuera del horario normal (22:00-06:00)
- **Estados**: APPROVED, PENDING, REJECTED
- **Endpoint de análisis**: GET `/api/fraud/analyze/{transactionId}`

### 4. Monitoreo y Métricas
- **Health Check**: GET `/actuator/health`
- **Métricas de transacciones**: GET `/api/metrics/transactions`
- **Stream de eventos**: GET `/api/events/stream`

## Entidades del Dominio

### Account
```java
- UUID id
- String accountNumber (único)
- String ownerName
- BigDecimal balance
- LocalDateTime createdAt
```

### Transaction
```java
- UUID id
- UUID fromAccountId
- UUID toAccountId
- BigDecimal amount
- LocalDateTime timestamp
- TransactionStatus status
- String fraudAnalysis (opcional)
```

### TransactionStatus (Enum)
```java
PENDING, APPROVED, REJECTED
```

## Casos de Uso Específicos

### Caso 1: Transferencia Simple
1. Usuario solicita transferir $100 de cuenta A a cuenta B
2. Sistema valida fondos suficientes
3. Sistema analiza fraude automáticamente
4. Si es aprobada: actualiza balances atómicamente
5. Retorna resultado de forma reactiva

### Caso 2: Detección de Fraude
1. Transacción de $6,000 (supera límite)
2. Sistema marca como PENDING
3. Análisis adicional requerido
4. Evento enviado a stream de monitoreo

### Caso 3: Múltiples Transacciones Concurrentes
1. Múltiples usuarios realizan transferencias simultáneas
2. Sistema maneja concurrencia sin bloqueos
3. Actualizaciones atómicas de balances
4. Consistencia de datos garantizada

## Endpoints Requeridos

### Cuentas
```
POST   /api/accounts              - Crear cuenta
GET    /api/accounts              - Listar cuentas
GET    /api/accounts/{id}         - Obtener cuenta
PUT    /api/accounts/{id}         - Actualizar cuenta
GET    /api/accounts/{id}/balance - Consultar balance
```

### Transacciones
```
POST   /api/transactions/transfer           - Realizar transferencia
GET    /api/transactions/{id}               - Obtener transacción
GET    /api/accounts/{id}/transactions      - Historial de cuenta
GET    /api/transactions/stream             - Stream en tiempo real
```

### Detección de Fraude
```
GET    /api/fraud/analyze/{transactionId}   - Analizar transacción
GET    /api/fraud/suspicious                - Listar sospechosas
```

## Validaciones Requeridas

### Transferencias
- Cuenta origen existe y tiene fondos suficientes
- Cuenta destino existe
- Monto > 0
- Cuentas origen y destino son diferentes

### Cuentas
- Número de cuenta único
- Nombre del propietario no vacío
- Balance inicial >= 0

## Manejo de Errores
- **404**: Cuenta no encontrada
- **400**: Validación fallida, fondos insuficientes
- **409**: Número de cuenta duplicado
- **500**: Error interno del servidor

## Pruebas Sugeridas
1. **Transferencia exitosa** entre dos cuentas
2. **Fondos insuficientes** (debe fallar)
3. **Cuenta inexistente** (debe fallar)
4. **Detección de fraude** por monto alto
5. **Múltiples transferencias concurrentes**
6. **Stream de transacciones en tiempo real**

## Extras (Opcional - Tiempo Permitiendo)
- **Paginación** en listados
- **Filtros** por fecha en historial
- **Métricas** con Micrometer
- **Tests de integración** con TestContainers
- **Rate limiting** por IP
- **Autenticación** JWT

## Entregables
1. **Código fuente** completo con estructura Maven
2. **Documentación** de endpoints (puede usar Swagger/OpenAPI)
3. **Scripts SQL** para inicialización
4. **README** con instrucciones de ejecución
5. **Tests unitarios** básicos

## Tiempo Estimado
- **Setup inicial y entidades**: 30 minutos
- **Repositories y servicios**: 60 minutos
- **Controllers y endpoints**: 40 minutos
- **Detección de fraude**: 40 minutos
- **Testing y refinamiento**: 30 minutos

¡Buena suerte implementando tu sistema bancario reactivo! 🚀 