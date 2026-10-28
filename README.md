# TV Maze API Middleware

API REST en Java que funciona como intermediario de los servicios de [TV Maze](https://www.tvmaze.com/api).
Permite buscar shows, consultar un show por id (con caché en MongoDB) y guardar calificaciones y comentarios.

## Tecnologías

- Java 17
- Spring Boot 3.5 (Web, Validation, Data MongoDB)
- `RestClient` de Spring para consumir TV Maze
- MongoDB Atlas
- JUnit 5, Mockito y MockMvc para pruebas

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/shows/search?search_query={texto}` | Busca shows y devuelve `id`, `name`, `channel`, `summary`, `genres` y `comments` |
| `GET` | `/api/shows/{show_id}` | Devuelve el show completo con su arreglo `comments`. Usa MongoDB como caché |
| `POST` | `/api/shows/{show_id}/comments` | Guarda un comentario y una calificación (0 a 5) para el show |

### A. Búsqueda

```bash
curl "http://localhost:8080/api/shows/search?search_query=girls"
```

```json
[
  {
    "id": 139,
    "name": "Girls",
    "channel": "HBO",
    "summary": "<p>This Emmy winning series is a comic look at ...</p>",
    "genres": ["Drama", "Romance"],
    "comments": [
      { "comment": "Muy buena serie", "rating": 5 }
    ]
  }
]
```

`channel` toma el nombre de `network` y, si el show no tiene cadena de TV, el de `webChannel`.

### B. Show por id

```bash
curl "http://localhost:8080/api/shows/139"
```

Devuelve el objeto completo de TV Maze más el arreglo `comments`.

Flujo de la caché:
1. Busca el id en la colección `shows` de MongoDB.
2. Si existe, devuelve ese objeto sin llamar a TV Maze.
3. Si no existe, consulta `https://api.tvmaze.com/shows/{show_id}`, guarda el resultado en `shows` y lo devuelve.
4. En ambos casos agrega los comentarios guardados antes de responder.

### C. Comentarios

```bash
curl -X POST "http://localhost:8080/api/shows/139/comments" \
  -H "Content-Type: application/json" \
  -d '{"comment": "Muy buena serie", "rating": 5}'
```

Respuesta `201 Created`:

```json
{ "status": "success", "message": "Comentario guardado correctamente", "id": "6720f1c2a1b2c3d4e5f60718" }
```

Validaciones: `comment` es obligatorio (máximo 1000 caracteres), `rating` es un entero de 0 a 5
y el show debe existir en TV Maze.

### Errores

Todos los errores comparten el mismo formato:

```json
{
  "status": "error",
  "code": 400,
  "message": "Datos inválidos",
  "details": ["rating debe estar entre 0 y 5"],
  "timestamp": "2026-10-27T18:00:00Z"
}
```

| Código | Caso |
|---|---|
| 400 | Parámetros faltantes o inválidos, JSON mal formado |
| 404 | El show no existe en TV Maze |
| 502 | TV Maze no está disponible o respondió con error |

## Configuración de MongoDB Atlas

1. Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) y un cluster gratuito (M0).
2. En **Database Access**, crea un usuario con contraseña.
3. En **Network Access**, agrega la IP `0.0.0.0/0` (*Allow access from anywhere*) para que no haya restricción de IP.
4. En **Connect → Drivers**, copia la cadena de conexión.
5. Copia `.env.example` como `.env` y reemplaza los datos:

```properties
MONGODB_URI=mongodb+srv://usuario:password@cluster0.xxxxx.mongodb.net/tvmaze?retryWrites=true&w=majority
```

El archivo `.env` está en `.gitignore`, así que las credenciales no se suben al repositorio.
También se puede definir `MONGODB_URI` como variable de entorno del sistema.

## Ejecución

Requisitos: Java 17 o superior y Maven 3.9+.

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Pruebas

```bash
mvn test
```

Las pruebas no necesitan conexión a MongoDB ni a TV Maze: los servicios se prueban con Mockito,
los controladores con MockMvc y el cliente HTTP con `MockRestServiceServer`.

## Estructura

```
src/main/java/com/evaluacion/tvmaze
├── client/        Cliente HTTP de TV Maze y sus DTOs
├── config/        Configuración del RestClient y propiedades
├── controller/    Endpoints REST
├── document/      Documentos de MongoDB (shows en caché y comentarios)
├── dto/           Objetos de entrada y salida del API
├── exception/     Excepciones y manejador global de errores
├── repository/    Repositorios de Spring Data MongoDB
└── service/       Lógica de negocio
```

## Decisiones de diseño

- **Show completo como mapa**: el endpoint B debe devolver el objeto completo de TV Maze, así que se guarda
  y devuelve como `Map<String, Object>` en lugar de modelar los más de 20 campos. El endpoint de búsqueda sí
  usa DTOs tipados porque solo necesita cinco campos.
- **`ShowCacheService` separado**: lo usan tanto `ShowService` como `CommentService` (para validar que el
  show exista antes de guardar un comentario) y evita una dependencia circular entre servicios.
- **Una sola consulta de comentarios en la búsqueda**: se obtienen los comentarios de todos los shows con
  `findByShowIdIn` en lugar de hacer una consulta por cada show.
- **La caché no se modifica**: el arreglo `comments` se agrega a una copia del objeto, para que los
  comentarios no queden congelados en la colección `shows`.
- **Credenciales fuera del código**: la cadena de conexión se lee de `MONGODB_URI`.
