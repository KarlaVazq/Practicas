# Proyecto: agrega Swagger, DTOs, códigos HTTP y seguridad JWT

Este README es la guía de trabajo para el proyecto base. Sigue los pasos en
orden y modifica únicamente los archivos de `demo/` que se indican. Al
terminar, la API tendrá documentación OpenAPI, DTOs validados, respuestas HTTP
adecuadas y autorización por roles con JWT.

> **Política de acceso:** todos los endpoints `GET` son públicos. Las
> operaciones de escritura requieren rol, excepto el registro y login.
>
> - Productos y categorías: escrituras solo para `ADMIN`.
> - Comentarios e imágenes: escrituras para `USER` o `ADMIN`.
> - `GET /productos` y `GET /comentarios`, al igual que todos los demás GET,
>   no requieren token.

## Contexto del proyecto

- Spring Boot 3.3.4, Java 21, Spring MVC, Spring Data JPA y PostgreSQL.
- Código Java: `demo/src/main/java/com/example/demo/`.
- Configuración: `demo/src/main/resources/application.properties`.
- Pruebas: `demo/src/test/java/com/example/demo/`.
- Las entidades y controladores actuales reciben y devuelven directamente
  modelos JPA.

## Antes de comenzar

1. Revisa el README del proyecto y las entidades en `demo/src/main/java/com/example/demo/model/`.
2. Confirma que PostgreSQL esté disponible y que las propiedades existentes
   de conexión funcionen.
3. Desde el directorio `demo/`, ejecuta:

   ```bash
   ./mvnw test
   ```

4. Anota cualquier falla previa. No cambies las relaciones de base de datos
   como parte de este ejercicio.

La carpeta `../Spring/` contiene una versión resuelta para comparar después
de intentar los pasos. Usa primero esta guía y deja la comparación para el
final.

## Paso 1: Agrega Swagger y OpenAPI

### 1.1 Agrega Springdoc

En `demo/pom.xml`, agrega las siguientes dependencias. Spring Boot administra
las versiones de sus starters:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

No dupliques dependencias que ya existan. Springdoc 2.x es la línea para
Spring Boot 3.

### 1.2 Crea la configuración OpenAPI

Crea `demo/src/main/java/com/example/demo/OpenApiConfiguration.java`, dentro
del paquete base de la aplicación. Define un título y una versión para la API,
y un esquema HTTP Bearer con formato JWT para que Swagger UI pueda enviar
tokens:

```java
package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI apiDocumentation() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Spring Authenticacion y DTO")
                        .description("API de productos, categorías, comentarios e imágenes")
                        .version("2.0.0"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
```

### 1.3 Documenta las operaciones

En cada controlador, agrega `@Tag` para agrupar operaciones y `@Operation`
para describir cada ruta. En las rutas protegidas, documenta también la
seguridad `bearerAuth`. Cada controlador y método deben conservar sus rutas
actuales salvo la corrección de imágenes del Paso 8.

Al finalizar, verifica:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Springdoc inspecciona las rutas de Spring MVC, anotaciones y tipos de
solicitud/respuesta. Publica el documento OpenAPI en `/v3/api-docs`; Swagger
UI lee ese documento y presenta una interfaz para explorar y probar la API.

## Paso 2: Devuelve los códigos HTTP correctos

Actualiza los métodos CRUD de los cuatro controladores para que devuelvan
`ResponseEntity<T>`. No regreses `Optional` ni un `String` que contenga JSON.

| Operación | Resultado esperado |
|---|---|
| GET de colección existente | `200 OK`, con lista o arreglo vacío `[]`. |
| GET de recurso existente | `200 OK`, con el recurso. |
| GET de recurso inexistente | `404 Not Found`. |
| POST exitoso | `201 Created`, cuerpo y encabezado `Location`. |
| PUT exitoso | `200 OK`, cuerpo actualizado. No debe crear si no existe. |
| DELETE exitoso | `204 No Content`, sin cuerpo. |
| PUT o DELETE de ID inexistente | `404 Not Found`. |
| Cuerpo inválido después de agregar validación | `400 Bad Request`. |
| Solicitud protegida sin token válido | `401 Unauthorized`. |
| Token válido con rol insuficiente | `403 Forbidden`. |

Para una consulta, convierte el `Optional` del repositorio en respuesta:

```java
return repo.findById(id)
        .map(value -> ResponseEntity.ok(value))
        .orElseGet(() -> ResponseEntity.notFound().build());
```

Para creación, guarda primero para obtener el ID generado:

```java
Producto saved = repo.save(producto);
URI location = URI.create("/producto/" + saved.getId());
return ResponseEntity.created(location).body(saved);
```

Antes de PUT y DELETE, comprueba que el ID exista. No uses `202 Accepted`
para una eliminación inmediata; usa `204 No Content`.

## Paso 3: Define DTOs para separar API y entidades

Un DTO (*Data Transfer Object*) define qué campos puede enviar y recibir el
cliente. No permitas que el cliente controle el ID generado ni las relaciones
JPA (`producto`, categorías, comentarios o imágenes relacionados).

### 3.1 Crea DTOs de producto

Crea `demo/src/main/java/com/example/demo/dto/ProductoRequest.java`:

```java
package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequest(
        @NotBlank String nombre,
        @NotBlank String descripcion,
        @NotBlank String lote,
        @NotNull @PositiveOrZero Float precioUnitario,
        @NotNull LocalDate fechaCaducidad) {
}
```

Usa `Float`, no `float`, en las entradas: el valor puede ser `null` y así la
validación puede detectar un dato omitido.

Crea `ProductoResponse.java` con el ID generado y los campos públicos del
producto. Agrega un método estático `from(Producto producto)` que convierta la
entidad en DTO. No incluyas sus colecciones JPA.

### 3.2 Crea DTOs para los demás recursos

En el mismo paquete `dto/`, agrega pares `Request` y `Response`:

| Recurso | Campos de entrada sugeridos | Validación mínima |
|---|---|---|
| Categoría | `nombre`, `descripcion`, `tipo`, `icono`, `imagenNavbar` | `nombre` no vacío. |
| Comentario | `username`, `comentario_corto`, `estrellas`, `comentario_largo`, `imagen1`, `imagen2` | Texto requerido; calificación de 1 a 5. |
| Imagen | `url`, `prioridad`, `tamanio`, `responsiva` | URL no vacía; prioridad no negativa. |

Cada `Response` debe exponer el ID y los campos adecuados, no la entidad
relacionada completa. Convierte a entidad mediante métodos como
`toEntity()` y actualiza una entidad ya cargada con métodos como
`updateEntity(...)`.

### 3.3 Migra los controladores a DTOs

En `ProductoController`, usa `ProductoRequest` para POST/PUT, y
`ProductoResponse` para las respuestas individuales y las listas. Aplica la
misma regla a `CategoriaController`, `ComentarioController` e
`ImagenController`.

Agrega `@Valid` a los cuerpos `@RequestBody`. Para las operaciones que reciben
un ID de producto, busca el producto en el servidor y asigna la relación; no
aceptes una entidad asociada desde el JSON.

Para las listas, mapea cada entidad:

```java
List<ProductoResponse> response = repo.findAll().stream()
        .map(ProductoResponse::from)
        .toList();
return ResponseEntity.ok(response);
```

Asegúrate de que las entidades tengan métodos de actualización para los
campos escalares necesarios. Cuando uses `@Builder` para una entidad que tiene
colecciones, inicializa esas colecciones con `@Builder.Default` y colecciones
vacías para evitar valores `null`.

## Paso 4: Diferencia PUT de PATCH

**PUT reemplaza todos los campos editables.** Requiere un cuerpo completo y
responde `404` cuando el recurso no existe.

**PATCH actualiza solo los campos enviados.** Crea
`ProductoPatchRequest.java` con campos opcionales (`String`, `Float`,
`LocalDate`) y validaciones que acepten `null` para representar “no enviado”.
Agrega un método `isEmpty()` para detectar un PATCH sin campos.

En `ProductoController`, agrega:

```java
@PatchMapping("producto/{id}")
public ResponseEntity<ProductoResponse> patchProducto(
        @PathVariable String id,
        @Valid @RequestBody ProductoPatchRequest patch) {
    // Rechaza patch vacío con 400.
    // Busca el producto; si falta, devuelve 404.
    // Cambia solo los campos no nulos, guarda y devuelve 200.
}
```

`@PathVariable` obtiene `id` de la URL; no significa PATCH. El nombre del
método HTTP es **PATCH**.

Prueba un cambio parcial:

```http
PATCH /producto/ID_PRODUCTO
Content-Type: application/json

{"nombre":"Nuevo nombre"}
```

Verifica que los demás valores no cambien. Esta estrategia interpreta `null`
como “no enviado”; por lo tanto, no permite borrar campos enviando `null`.

## Paso 5: Agrega cuentas persistidas

Crea la entidad `AppUser` en el paquete `model/` con:

- `id` generado;
- `username` único y obligatorio;
- `passwordHash` obligatorio;
- `role` obligatorio.

Crea `appUserRepo` en el paquete `repo/`, extendiendo
`JpaRepository<AppUser, String>`, con una búsqueda por username:

```java
Optional<AppUser> findByUsername(String username);
```

Hibernate creará/actualizará la tabla `app_users` de acuerdo con la
configuración JPA existente.

## Paso 6: Implementa registro, login y emisión del JWT

### 6.1 DTOs de autenticación

En `dto/`, crea:

- `RegisterRequest`: `username` obligatorio (3–50 caracteres), `password`
  obligatorio (8–100 caracteres).
- `LoginRequest`: username y password obligatorios.
- `UserResponse`: ID, username y rol; nunca la contraseña/hash.
- `AuthResponse`: `accessToken` y `tokenType`.

### 6.2 Registro

Crea `AuthController` con `POST /auth/register`. Comprueba si el username ya
existe (`409 Conflict`), codifica la contraseña con `BCryptPasswordEncoder`
y guarda siempre el rol `USER`. Ignora cualquier rol enviado por el cliente.
Devuelve `201 Created` y un `UserResponse`.

No agregues un endpoint público para crear administradores.

### 6.3 Login

Agrega `POST /auth/login`. Autentica las credenciales con
`AuthenticationManager`. Si son correctas, llama a `JwtTokenService` y
devuelve `200 OK` con:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer"
}
```

Si las credenciales no son válidas, devuelve `401 Unauthorized`.

### 6.4 Firma y contenido del JWT

Crea `service/JwtTokenService.java`. Usa un `JwtEncoder` y firma con HS256.
Incluye estos claims:

- `sub`: username;
- `roles`: roles sin el prefijo `ROLE_`;
- `iat`: hora de emisión;
- `exp`: expiración, por ejemplo una hora;
- `iss`: identificador de esta API.

El claim `roles` es el que permitirá autorizar las operaciones después.

## Paso 7: Configura Resource Server y permisos

Crea `SecurityConfig.java` en `com.example.demo`. Activa
`@EnableMethodSecurity`, deshabilita sesiones (stateless), configura el
`JwtEncoder`, `JwtDecoder`, `PasswordEncoder`, `AuthenticationManager` y
`JwtAuthenticationConverter`.

El conversor debe leer el claim `roles` y agregar el prefijo `ROLE_` para que
Spring reconozca `ADMIN` como `ROLE_ADMIN` y `USER` como `ROLE_USER`.

Configura las reglas del filtro en este orden:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
    .requestMatchers(HttpMethod.GET, "/**").permitAll()
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .anyRequest().authenticated())
.oauth2ResourceServer(oauth2 -> oauth2.jwt(...))
```

El matcher GET hace públicas **todas** las lecturas, incluyendo búsquedas por
ID y listas relacionadas. Las escrituras requieren un JWT.

Agrega `@PreAuthorize` a nivel de clase:

| Controlador | Regla |
|---|---|
| `ProductoController` | `hasRole('ADMIN')` |
| `CategoriaController` | `hasRole('ADMIN')` |
| `ComentarioController` | `hasAnyRole('USER', 'ADMIN')` |
| `ImagenController` | `hasAnyRole('USER', 'ADMIN')` |

Como el método GET debe poder sobreescribir la regla de clase, agrega
`@PreAuthorize("permitAll()")` en **cada método** anotado con `@GetMapping`.
Así las rutas GET son públicas aun si el controlador tiene autorización de
clase.

CSRF se desactiva porque la API es stateless y autentica operaciones de
escritura con Bearer tokens explícitos, no cookies de sesión. Usa HTTPS en
despliegues reales.

### Clave JWT

Configura opcionalmente `DEMO_JWT_SECRET` como clave aleatoria codificada en
Base64 de al menos 32 bytes. No confundas esta clave de firma con contraseñas
de usuario ni la subas al repositorio.

Si no defines la variable, el programa puede generar una clave aleatoria al
iniciar; esto sirve para desarrollo, pero los tokens dejarán de funcionar
después de reiniciar la aplicación. Para conservarlos durante pruebas locales:

```bash
export DEMO_JWT_SECRET="$(openssl rand -base64 32)"
```

No se necesitan variables de entorno `DEMO_ADMIN_PASSWORD` ni
`DEMO_USER_PASSWORD`: las cuentas se registran en la aplicación.

## Paso 8: Corrige la ruta GET duplicada de imágenes

El controlador base declara dos mappings GET con patrón `/imagen/{...}`:
buscar una imagen por ID y listar las imágenes de un producto. Spring MVC no
puede diferenciarlos por el nombre del argumento.

Mantén `GET /imagen/{id}` para una imagen y cambia la lista del producto a:

```text
GET /imagenes/producto/{productId}
```

Si la ruta de creación asociada a producto se conserva en
`POST /imagen/{productId}`, no entra en conflicto: el método HTTP es distinto.

## Paso 9: Documenta la seguridad en Swagger

Define `bearerAuth` como esquema HTTP Bearer con formato JWT en
`OpenApiConfiguration`. Agrega `@SecurityRequirement(name = "bearerAuth")` a
los controladores y `@SecurityRequirements` vacío en cada GET público para
que Swagger UI no solicite token en esas operaciones.

Las rutas de documentación deben estar permitidas en `SecurityConfig`. En
Swagger UI, usa **Authorize** y pega el token emitido por `/auth/login`.

## Paso 10: Prueba manualmente

Inicia PostgreSQL y la aplicación:

```bash
cd demo
./mvnw spring-boot:run
```

No se requieren credenciales de usuario configuradas en variables de entorno.
Registra una cuenta `USER` e inicia sesión:

```bash
curl -i -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"estudiante","password":"clave-local-segura-123"}'

curl -i -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"estudiante","password":"clave-local-segura-123"}'
```

Usa `accessToken` de la segunda respuesta:

```bash
curl -i http://localhost:8080/imagenes \
  -H 'Authorization: Bearer PEGA_AQUI_EL_TOKEN'
```

Comprueba que se puede hacer **cada GET sin token**:

```bash
curl -i http://localhost:8080/productos
curl -i http://localhost:8080/producto/ID_PRODUCTO
curl -i http://localhost:8080/comentarios
curl -i http://localhost:8080/comentarios/ID_PRODUCTO
curl -i http://localhost:8080/comentario/ID_COMENTARIO
curl -i http://localhost:8080/categorias
curl -i http://localhost:8080/categoria/ID_CATEGORIA
curl -i http://localhost:8080/categoria/producto/ID_PRODUCTO
curl -i http://localhost:8080/imagenes
curl -i http://localhost:8080/imagenes/producto/ID_PRODUCTO
curl -i http://localhost:8080/imagen/ID_IMAGEN
```

Los IDs inexistentes deben llegar al controlador y devolver `404`, no `401`.
Las colecciones vacías devuelven `200` con `[]`.

Para probar escrituras:

1. Sin token, intenta `POST /producto`: debe devolver `401`.
2. Usa el token de `USER` para intentar crear un producto: debe devolver
   `403`.
3. Usa el token `USER` en una escritura de comentarios o imágenes: debe
   permitirse si el cuerpo es válido.
4. Para probar permisos `ADMIN`, cambia el rol de una cuenta por un proceso
   administrativo confiable en la base de datos y vuelve a iniciar sesión:

   ```sql
   UPDATE app_users
   SET role = 'ADMIN'
   WHERE username = 'nombre-de-la-cuenta';
   ```

5. Usa el JWT nuevo para crear/actualizar/eliminar productos y categorías.
   No expongas una ruta pública de promoción de usuarios.

Prueba también entradas inválidas (`400`), ID inexistente (`404`), creación
exitosa (`201` y `Location`), actualización (`200`) y eliminación (`204`).

## Paso 11: Ejecuta las pruebas

Desde `demo/`:

```bash
./mvnw test
```

Agrega pruebas de integración que cubran:

- todos los endpoints GET responden sin token;
- una escritura protegida sin token responde `401`;
- un token de `USER` recibe `403` al escribir productos/categorías;
- `USER` o `ADMIN` puede escribir comentarios/imágenes;
- registro siempre crea rol `USER`;
- login emite JWT válido y uno inválido responde `401`;
- DTO inválido responde `400`;
- PATCH modifica solo los campos enviados;
- OpenAPI publica el esquema Bearer JWT.

## Checklist final

- [x] Swagger UI y `/v3/api-docs` cargan.
- [x] Los controladores usan DTOs en lugar de entidades JPA en sus contratos.
- [x] Las validaciones inválidas responden `400`.
- [x] PUT reemplaza campos editables y no crea recursos nuevos.
- [x] PATCH conserva los campos no enviados.
- [x] Todos los GET son públicos.
- [x] POST/PUT/PATCH/DELETE conservan autorización por rol.
- [x] Registro crea solo `USER`; login emite JWT Bearer.
- [x] La clave JWT se mantiene fuera del código.
- [x] No hay mappings GET duplicados para imágenes.
- [x] `./mvnw test` pasa.

## Referencia

La documentación OpenAPI toma como referencia
[Using Swagger 3 in Spring Boot 3](https://medium.com/@f.s.a.kuzman/using-swagger-3-in-spring-boot-3-c11a483ea6dc).
El flujo JWT se basa en conceptos de
[Implementing User Authentication in a Spring Boot Application](https://techoguide.medium.com/implementing-user-authentication-in-a-spring-boot-application-a-detailed-step-by-step-guide-f669f8943e1a).

---

## Resolución del ejercicio

La solución completa está en `demo/`. Resumen por paso:

| Paso | Qué se hizo | Archivos principales |
|---|---|---|
| 1 | Springdoc `2.6.0` y configuración OpenAPI con esquema Bearer JWT. | `pom.xml`, `OpenApiConfiguration.java` |
| 2 | Todos los controladores devuelven `ResponseEntity` con `200/201/204/404`. | `controllers/*Controller.java` |
| 3 | DTOs `Request`/`Response` con validación (`@Valid`); el cliente no controla IDs ni relaciones. | `dto/*.java` |
| 4 | `PUT` reemplaza todo y no crea; `PATCH` cambia solo los campos enviados (`400` si viene vacío). | `ProductoController`, `ProductoPatchRequest` |
| 5 | Cuentas persistidas en la tabla `app_users`. | `model/AppUser.java`, `repo/appUserRepo.java` |
| 6 | `POST /auth/register` (siempre rol `USER`, `409` si existe) y `POST /auth/login` (JWT HS256, 1 hora). | `AuthController`, `JwtTokenService` |
| 7 | Resource Server stateless; claim `roles` convertido a `ROLE_*`; `@PreAuthorize` por controlador y `permitAll()` en cada GET. | `SecurityConfig.java` |
| 8 | Ruta duplicada corregida: `GET /imagenes/producto/{productId}`. | `ImagenController` |
| 9 | Swagger con botón **Authorize**; GET públicos sin candado. | `@SecurityRequirement(s)` |
| 10-11 | Pruebas de integración con H2 (perfil `test`). | `src/test/.../ApiSecurityTests.java` |

**Política de acceso resultante**

| Recurso | GET | POST / PUT / PATCH / DELETE |
|---|---|---|
| Productos y categorías | Público | Solo `ADMIN` |
| Comentarios e imágenes | Público | `USER` o `ADMIN` |
| `/auth/register`, `/auth/login` | n/a | Público |

**Comprobaciones cubiertas por las pruebas:** GET sin token (`200`/`404`), escritura sin token (`401`), `USER` en productos (`403`), `ADMIN` crea con `Location` y hace PATCH parcial, DTO inválido (`400`), registro siempre con rol `USER`, login válido/inválido y esquema Bearer en `/v3/api-docs`.

**Ejecución**

```bash
cd demo
export DEMO_JWT_SECRET="$(openssl rand -base64 32)"   # opcional, para conservar tokens al reiniciar
./mvnw test
./mvnw spring-boot:run
```
