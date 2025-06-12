# 🏦 Sistema Bancario Reactivo

## Descripción
Sistema bancario completo implementado con **Spring Boot 3**, **WebFlux**, **R2DBC** y patrones reactivos. El sistema incluye gestión de cuentas, transacciones y detección de fraude en tiempo real.

## 🚀 Stack Tecnológico

- **Spring Boot 3.2.5** - Framework principal
- **Spring WebFlux** - APIs REST reactivas  
- **Spring Data R2DBC** - Acceso a datos reactivo
- **H2 Database** - Base de datos en memoria
- **Reactor Core** - Programación reactiva
- **Lombok** - Reducción de boilerplate
- **Swagger/OpenAPI** - Documentación de APIs
- **Maven** - Gestión de dependencias

## 📋 Características

### ✅ Gestión de Cuentas
- Crear nuevas cuentas bancarias
- Consultar información de cuentas
- Actualizar datos de cuentas
- Consultar balance actual

### 💸 Sistema de Transacciones
- Transferencias entre cuentas
- Validación de fondos suficientes
- Historial de transacciones
- Stream en tiempo real (Server-Sent Events)

### 🛡️ Detección de Fraude
- **Análisis automático** de cada transacción
- **Reglas implementadas:**
  - Transacciones > $5,000 (sospechosas)
  - Más de 3 transacciones por minuto
  - Transacciones en horario nocturno (22:00-06:00)
- Estados: `PENDING`, `APPROVED`, `REJECTED`

## 🏃‍♂️ Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+

### Ejecutar la aplicación
```bash
# Clonar y navegar al directorio
cd reactive-banking

# Ejecutar con Maven
mvn spring-boot:run

# O compilar y ejecutar JAR
mvn clean package
java -jar target/reactive-banking-1.0.0-SNAPSHOT.jar
```

### URLs Importantes
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

## 🌐 API Endpoints

### Cuentas (`/api/accounts`)
```http
GET    /api/accounts              # Listar todas las cuentas
POST   /api/accounts              # Crear nueva cuenta
GET    /api/accounts/{id}         # Obtener cuenta por ID
PUT    /api/accounts/{id}         # Actualizar cuenta
GET    /api/accounts/{id}/balance # Consultar balance
```

### Transacciones (`/api/transactions`)
```http
POST   /api/transactions/transfer           # Realizar transferencia
GET    /api/transactions/{id}               # Obtener transacción
GET    /api/transactions/account/{id}       # Historial de cuenta
GET    /api/transactions/stream             # Stream tiempo real (SSE)
GET    /api/transactions/latest?limit=10    # Últimas transacciones
```

### Detección de Fraude (`/api/fraud`)
```http
GET    /api/fraud/analyze/{transactionId}   # Analizar transacción
GET    /api/fraud/suspicious                # Listar sospechosas
```

## 📝 Ejemplos de Uso

### 1. Crear una cuenta
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC123",
    "ownerName": "Juan Pérez",
    "balance": 1000.00
  }'
```

### 2. Realizar transferencia
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 100.00,
    "description": "Pago de servicios"
  }'
```

### 3. Stream de transacciones en tiempo real
```bash
curl -N http://localhost:8080/api/transactions/stream
```

### 4. Consultar transacciones sospechosas
```bash
curl http://localhost:8080/api/fraud/suspicious
```

## 🧪 Testing

### Ejecutar tests
```bash
mvn test
```

### Tests incluidos
- **Servicios**: Pruebas unitarias con Mockito
- **Repositorios**: Tests reactivos con StepVerifier
- **APIs**: Tests de integración con WebTestClient

## 🛠️ Configuración

### Base de datos (application.yml)
```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///bankingdb
    username: sa
    password:
```

### Detección de fraude
```yaml
banking:
  fraud:
    high-amount-threshold: 5000.00
    max-transactions-per-minute: 3
    suspicious-hours:
      start: 22
      end: 6
```

## 📊 Datos de Prueba

El sistema incluye datos iniciales:

**Cuentas:**
- `ACC001`: Juan Pérez - $10,000
- `ACC002`: María García - $5,000  
- `ACC003`: Carlos López - $15,000
- `ACC004`: Ana Martínez - $2,500
- `ACC005`: Luis Rodríguez - $7,500

## 🔧 Arquitectura

```
┌─────────────────┐
│   Controllers   │ ← WebFlux REST APIs
├─────────────────┤
│    Services     │ ← Lógica de negocio reactiva
├─────────────────┤
│  Repositories   │ ← R2DBC Reactive Data Access
├─────────────────┤
│   Database      │ ← H2 In-Memory Database
└─────────────────┘
```

### Patrones Implementados
- **Repository Pattern** con Spring Data R2DBC
- **DTO Pattern** para transferencia de datos
- **Global Exception Handler** para manejo de errores
- **Reactive Streams** con Mono y Flux
- **Event Streaming** con Sinks para tiempo real

## 🚨 Manejo de Errores

- **404**: Recurso no encontrado
- **400**: Datos inválidos, fondos insuficientes
- **409**: Recurso duplicado
- **500**: Error interno del servidor

Todas las respuestas de error incluyen:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Fondos insuficientes",
  "path": "/api/transactions"
}
```

## 🎯 Casos de Uso para la Clase

### Caso 1: Transferencia Normal ✅
```bash
# 1. Consultar cuentas disponibles
curl http://localhost:8080/api/accounts

# 2. Realizar transferencia de $100
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001", 
    "amount": 100.00,
    "description": "Transferencia normal"
  }'

# 3. Verificar balances actualizados
curl http://localhost:8080/api/accounts/550e8400-e29b-41d4-a716-446655440000/balance
```

### Caso 2: Detección de Fraude por Alto Monto 🚨
```bash
# Transferencia de $6,000 (supera límite de $5,000)
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 6000.00,
    "description": "Transferencia de alto monto"
  }'

# Verificar que queda PENDING y no se ejecuta
curl http://localhost:8080/api/fraud/suspicious
```

### Caso 3: Fondos Insuficientes ❌
```bash
# Intentar transferir más dinero del disponible
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440003",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 5000.00,
    "description": "Transferencia con fondos insuficientes"
  }'
```

### Caso 4: Stream en Tiempo Real 📡
```bash
# En una terminal, abrir el stream
curl -N http://localhost:8080/api/transactions/stream

# En otra terminal, realizar transferencias
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 50.00,
    "description": "Transferencia en vivo"
  }'
```

## 🎓 Conceptos Reactivos Demostrados

- **Mono/Flux**: Tipos reactivos básicos
- **Flatmap**: Composición de operaciones asíncronas
- **SwitchIfEmpty**: Manejo de casos vacíos
- **DoOnNext/DoOnSuccess**: Side effects
- **Error Handling**: onErrorMap, onErrorReturn
- **Backpressure**: Control de flujo
- **Hot Streams**: Server-Sent Events
- **Reactive Repositories**: R2DBC
- **Reactive Testing**: StepVerifier

## 🤝 Contribuir

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/nueva-feature`)
3. Commit cambios (`git commit -am 'Agregar nueva feature'`)
4. Push a la rama (`git push origin feature/nueva-feature`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

**¡Sistema listo para la clase de Spring Reactive! 🚀**

Para cualquier duda durante la implementación, consultar la documentación interactiva en: http://localhost:8080/swagger-ui.html 