# Java Reactive Programming - Spring Boot 3 WebFlux

Esta aplicación expone APIs para aprender programación reactiva usando Spring Boot 3 y WebFlux.

## Descripción

La aplicación implementa múltiples demos que demuestran diferentes aspectos de la programación reactiva:

- **Demo01**: Servicio básico de productos
- **Demo02**: Servicios de streaming (nombres y precios de acciones)  
- **Demo03**: Servicios con patrones de fallback (timeout, empty)
- **Demo04**: Stream de órdenes
- **Demo05**: Servicios con delays simulados (productos, precios, reseñas)
- **Demo06**: Servicios con simulación de errores
- **Demo07**: Servicio de libros aleatorios

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior

## Instalación y Ejecución

1. **Clonar el repositorio** (si aplica):
   ```bash
   git clone <repository-url>
   cd external-service
   ```

2. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

   O alternativamente:
   ```bash
   mvn clean package
   java -jar target/external-service-1.0.0-SNAPSHOT.jar
   ```

4. **Verificar que la aplicación esté ejecutándose**:
   - La aplicación se ejecutará en: `http://localhost:7070`
   - OpenAPI/Swagger UI: `http://localhost:7070/swagger-ui.html`
   - Especificación OpenAPI JSON: `http://localhost:7070/v3/api-docs`

## Endpoints Disponibles

### Demo01 - Servicio Básico de Productos
- `GET /demo01/product/{id}` - Obtiene el nombre del producto (IDs 1-100)

### Demo02 - Servicios de Streaming
- `GET /demo02/name/stream` - Stream de nombres aleatorios cada 500ms
- `GET /demo02/stock/stream` - Stream de precios de acciones cada 500ms (rango: 80-120)

### Demo03 - Servicios con Fallback
- `GET /demo03/product/{id}` - Producto con fallback automático (IDs 1-4)
- `GET /demo03/timeout-fallback/product/{id}` - Simula timeout y fallback
- `GET /demo03/empty-fallback/product/{id}` - Simula respuesta vacía con fallback

### Demo04 - Stream de Órdenes
- `GET /demo04/orders/stream` - Stream de órdenes simuladas

### Demo05 - Servicios con Delay (1 segundo)
- `GET /demo05/product/{id}` - Nombre del producto con delay (IDs 1-10)
- `GET /demo05/price/{id}` - Precio del producto con delay (IDs 1-10)
- `GET /demo05/review/{id}` - Reseña del producto con delay (IDs 1-10)

### Demo06 - Servicios con Simulación de Errores
- `GET /demo06/country` - Nombre de país aleatorio (respuesta en 100ms)
- `GET /demo06/product/{id}` - Producto con errores simulados:
  - ID 1: Siempre retorna Bad Request
  - ID 2: Error aleatorio
  - Otros IDs: Respuesta normal

### Demo07 - Servicio de Libros
- `GET /demo07/book` - Nombre de libro aleatorio

## Características de Programación Reactiva Implementadas

1. **Mono y Flux**: Uso de tipos reactivos para operaciones síncronas y asíncronas
2. **Streaming**: Endpoints que emiten datos continuamente
3. **Timeouts**: Manejo de timeouts con fallbacks
4. **Error Handling**: Diferentes estrategias de manejo de errores
5. **Delays**: Simulación de latencia en servicios
6. **Backpressure**: Manejo automático con WebFlux

## Ejemplos de Uso

### Consumir un endpoint simple:
```bash
curl http://localhost:7070/demo01/product/1
```

### Consumir un stream (con curl):
```bash
curl http://localhost:7070/demo02/name/stream
curl http://localhost:7070/demo02/stock/stream
```

### Probar el manejo de errores:
```bash
curl http://localhost:7070/demo06/product/1  # Bad Request
curl http://localhost:7070/demo06/product/2  # Error aleatorio
curl http://localhost:7070/demo06/product/3  # Respuesta normal
```

## Herramientas de Desarrollo

- **Swagger UI**: Interfaz interactiva para probar los endpoints
- **OpenAPI 3**: Especificación completa de la API
- **Spring Boot Actuator**: Endpoints de monitoreo (si se habilita)

## Notas Técnicas

- La aplicación usa Spring Boot 3.2.12 con WebFlux
- Todos los endpoints son no-bloqueantes y reactivos
- Los streams tienen duraciones limitadas para evitar conexiones infinitas
- Se incluye simulación realista de delays y errores para propósitos educativos
- El stockStream emite precios numéricos cada 500ms en formato de texto plano

## Debugging del Stock Stream

Para verificar que el stock stream funciona correctamente:

```bash
# Ver los logs en la consola cuando se ejecuta el stream
curl http://localhost:7070/demo02/stock/stream

# También puedes usar herramientas como httpie
http --stream http://localhost:7070/demo02/stock/stream
```

## Autor

- **vinsguru** - [https://www.vinsguru.com](https://www.vinsguru.com)

## Licencia

Este proyecto es para fines educativos en programación reactiva con Spring WebFlux. 