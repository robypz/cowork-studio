# ADR-0001: DDD + CQRS (completo) + Arquitectura Hexagonal para cowork-studio MVP

**Status:** Proposed
**Date:** 2026-07-10
**Deciders:** Robert Yepez (solo dev)

## Contexto

cowork-studio es una API Spring Boot (Java 21, Spring Boot 4, JPA/Postgres, Flyway, Security + JWT, springdoc-openapi) para administrar un co-working con modelo de reservas por uso (sin membresías). El MVP definido cubre 4 áreas: Identity & Access (usuarios/auth), Spaces (espacios reservables), Availability (disponibilidad) y Booking (reservas).

El proyecto lo desarrolla una sola persona, con experiencia parcial previa en DDD/CQRS/Hexagonal, y tiene doble objetivo explícito: (1) demostrar en portfolio dominio real de estos patrones — no una implementación superficial — y (2) dejar una base capaz de evolucionar a un producto comercial (multi-sede, multi-tenant, más módulos de negocio) sin reescritura mayor. No hay restricciones de tiempo ni de escala conocidas.

Dado ese doble objetivo, el criterio de decisión no es "qué es lo mínimo para 4 módulos", sino "qué arquitectura resiste tanto una evaluación técnica seria como el crecimiento hacia un producto real". Eso descarta de entrada una solución puramente minimalista aunque el alcance funcional del MVP sea pequeño.

## Decisión

Adoptar:

1. **Hexagonal (puertos y adaptadores)** como esqueleto de cada módulo: dominio en el centro; la capa de aplicación orquesta casos de uso a través de puertos (interfaces); la infraestructura (REST, JPA, seguridad) implementa esos puertos.
2. **DDD táctico**, mapeando los 4 módulos a bounded contexts: `identity`, `spaces`, `booking`. Availability se modela como parte del dominio de Booking (regla de negocio del agregado, no un contexto propio).
3. **CQRS completo dentro de cada contexto**: comandos y queries se separan en la capa de aplicación. El lado de escritura usa entidades de dominio ricas (agregados); el lado de lectura usa modelos propios (tablas/proyecciones denormalizadas) optimizados para las consultas de la API. Ambos viven en la misma base de datos Postgres — sin segunda BD ni bus de eventos.
4. Sincronización lectura/escritura **dentro de la misma transacción** (no eventual consistency vía mensajería). Al ser una sola BD y un solo proceso, no se justifica el costo operativo de consistencia eventual en el MVP.

## Opciones consideradas

### Opción A: Capas tradicionales (Controller → Service → Repository → Entity)

| Dimensión | Evaluación |
|---|---|
| Complejidad | Baja |
| Costo (tiempo de desarrollo) | Bajo para solo dev |
| Escalabilidad | Suficiente para 4 módulos |
| Familiaridad | Alta (patrón muy conocido) |

**Pros:** rápido de escribir, pocos archivos, Spring Data JPA cubre casi todo out-of-the-box.
**Contras:** las entidades JPA hacen doble función (dominio + persistencia); la lógica de negocio tiende a filtrarse al Service o al Controller; las queries de lectura complejas contaminan el modelo de escritura. No sirve a ninguno de los dos objetivos del proyecto: no demuestra los patrones que se quieren mostrar en portfolio, y migrar de esto a algo más robusto más adelante implica reescribir, no extender.

### Opción B: DDD + Hexagonal, sin CQRS (un solo modelo)

| Dimensión | Evaluación |
|---|---|
| Complejidad | Media |
| Costo | Medio |
| Escalabilidad | Buena — dominio aislado del framework |
| Familiaridad | Media (ya hay algo de experiencia) |

**Pros:** dominio limpio y testeable sin Spring; buen valor de portfolio; separación clara negocio/infraestructura.
**Contras:** demuestra DDD y Hexagonal, pero deja fuera CQRS — si el objetivo es mostrar dominio de los tres patrones, esta opción se queda corta. Además, un solo modelo debe servir tanto para comandos (invariantes) como para queries (forma que necesita el API/frontend): en endpoints de listado (ej. reservas de un espacio en un rango de fechas, con nombre de usuario y espacio) se termina exponiendo el agregado completo o armando DTOs ad-hoc sin un patrón consistente — justo el problema que CQRS resuelve.

### Opción C (elegida): DDD + Hexagonal + CQRS completo, misma BD, sincrónico

| Dimensión | Evaluación |
|---|---|
| Complejidad | Alta |
| Costo | Alto — cada caso de uso implica comando/handler y su proyección de lectura |
| Escalabilidad | Muy buena — el lado de lectura se optimiza/cachea/particiona sin tocar el dominio |
| Familiaridad | Media-baja, pero es objetivo explícito de aprendizaje |

**Pros:** demuestra los tres patrones de forma completa y coherente para portfolio; separación total entre invariantes de negocio (escritura) y necesidades de presentación (lectura); las queries del API (disponibilidad, listados de reservas, futuro dashboard de admin) se sirven desde tablas ya denormalizadas, sin joins complejos en el hot path; si el proyecto escala a producto comercial (más módulos, más carga de lectura por sede/tenant), el lado de lectura ya está desacoplado y listo para optimizarse sin tocar el dominio; sin bus de eventos ni infraestructura extra porque todo vive en la misma transacción/BD, así que el costo de entrada es solo de diseño, no operativo.
**Contras:** más código por feature (comando, handler, agregado, puerto, adaptador de escritura, modelo de lectura, adaptador de lectura); mantener las proyecciones de lectura sincronizadas agrega responsabilidad extra en cada comando. Este costo es aceptable aquí porque es precisamente el que se está dispuesto a pagar a cambio de los dos objetivos del proyecto (portfolio + base escalable a producto real).

## Análisis de trade-offs

La Opción C cuesta más en velocidad inicial: cada caso de uso ("crear reserva") toca más archivos que en un CRUD tradicional. Ese costo se acepta a propósito porque es la línea que separa "un CRUD más" de "un proyecto que demuestra DDD/CQRS/Hexagonal en serio" y porque sienta las bases para escalar a producto. Donde sí se traza el límite es en no acumular complejidad que no aporta a ninguno de los dos objetivos:

- Sincronía dentro de la misma transacción evita eventual consistency, sagas o bus de mensajes. Esto no resta valor de portfolio (CQRS no exige mensajería para ser CQRS) y sí evita infraestructura operativa que un solo dev tendría que mantener sin necesidad real todavía. Si el proyecto escala a multi-sede/alta concurrencia, ese es el punto natural para introducir consistencia eventual — la separación de modelos ya hecha ahora hace ese paso incremental, no una reescritura.
- No se hace Event Sourcing todavía. Los agregados se persisten como estado actual. "CQRS completo" aquí significa modelos de lectura/escritura separados, no Event Sourcing — son decisiones independientes, y Event Sourcing puede añadirse después sin invalidar esta base si el caso de uso lo justifica (ej. auditoría de reservas).
- Los 3 bounded contexts (identity, spaces, booking) ya surgen de los módulos definidos previamente — no hay trabajo adicional de "descubrir" contextos.

## Consecuencias

**Se vuelve más fácil:** escalar el lado de lectura de forma independiente (índices, vistas materializadas, incluso un read replica más adelante); testear el dominio sin levantar el contexto de Spring; extraer un bounded context a un servicio separado si el co-working crece a multi-sede.

**Se vuelve más difícil:** hay más boilerplate por feature; se requiere disciplina para no acceder a las tablas de lectura desde el lado de escritura (o viceversa); el onboarding de un futuro colaborador exige explicar el patrón.

**A revisar más adelante:** en qué punto de crecimiento (más sedes, más carga, necesidad de auditoría) conviene pasar de sincronía en la misma transacción a proyecciones asíncronas vía eventos de dominio — la separación de modelos hecha ahora es justamente lo que hace ese paso incremental cuando llegue el momento.

## Estructura de paquetes propuesta

```
com.orinocolabs.cowork_studio
├── shared/
│   ├── domain/          (Value Objects comunes: Money, DateRange, etc.)
│   └── infrastructure/  (config JWT, OpenAPI, manejo global de excepciones)
│
├── identity/
│   ├── domain/          (User, Role — agregado, puertos: UserRepository)
│   ├── application/
│   │   ├── command/     (RegisterUserCommand + Handler, LoginCommand + Handler)
│   │   └── query/       (GetUserProfileQuery + Handler -> UserProfileView)
│   └── infrastructure/
│       ├── in/web/      (AuthController, UserController)
│       └── out/persistence/
│           ├── write/   (UserJpaEntity, UserRepositoryAdapter)
│           └── read/    (UserProfileView — proyección de solo lectura)
│
├── spaces/
│   ├── domain/          (Space — agregado, Value Objects: Capacity, HourlyRate)
│   ├── application/
│   │   ├── command/     (CreateSpaceCommand, UpdateSpaceCommand, ...)
│   │   └── query/       (ListAvailableSpacesQuery -> SpaceListItemView)
│   └── infrastructure/
│       ├── in/web/
│       └── out/persistence/{write,read}/
│
└── booking/
    ├── domain/          (Booking — agregado raíz; reglas de disponibilidad/solapamiento viven aquí)
    ├── application/
    │   ├── command/     (CreateBookingCommand, CancelBookingCommand)
    │   └── query/       (GetSpaceAvailabilityQuery, ListMyBookingsQuery -> BookingListView)
    └── infrastructure/
        ├── in/web/
        └── out/persistence/{write,read}/
```

Cada `command/*Handler` depende solo de puertos de dominio (interfaces definidas en `domain/`); los adaptadores en `infrastructure/out/persistence/write` los implementan con JPA. Cada `query/*Handler` depende de puertos de lectura definidos en `application/query/`, implementados por adaptadores en `infrastructure/out/persistence/read`, que pueden usar JPQL/proyecciones nativas directamente sin pasar por el agregado de dominio.

## Action Items

1. [ ] Crear estructura de paquetes `shared / identity / spaces / booking` en `src/main/java/com/orinocolabs/cowork_studio`
2. [ ] Definir agregados de dominio: `User`, `Space`, `Booking` (sin anotaciones JPA en el dominio puro)
3. [ ] Definir puertos de escritura (`UserRepository`, `SpaceRepository`, `BookingRepository`) como interfaces de dominio
4. [ ] Definir puertos de lectura (`UserProfileFinder`, `SpaceListFinder`, `BookingListFinder`) con sus DTOs de vista
5. [ ] Implementar adaptadores JPA de escritura (entidades ricas) y de lectura (proyecciones/DTOs)
6. [ ] Migraciones Flyway: tablas de escritura normalizadas + tablas/vistas de lectura denormalizadas
7. [ ] Definir convención de manejo de errores de dominio → respuestas HTTP en el adaptador `in/web`
