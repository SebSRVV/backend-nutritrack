# 🍏 NutriTrack API (Backend)

Backend construido en **Spring Boot 3 + Supabase** para la app NutriTrack.  
Expone endpoints REST para autenticación y gestión de perfiles de usuarios.

---

## 🚀 Tecnologías

- [Spring Boot 3](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Supabase Auth & DB](https://supabase.com)
- [WebFlux (Reactive)](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- Deploy en [Render](https://render.com)

---

Aquí tienes una propuesta de actualización para el **README** (sección de configuración + uso) alineada con tu `application.properties` actual y el soporte **JWT HS256** de Supabase.

---

# ⚙️ Configuración

### `application.properties` (referencia)

```properties
# --- Supabase ---
supabase.url=${SUPABASE_URL}
supabase.serviceKey=${SUPABASE_SERVICE_ROLE_KEY}
supabase.anonKey=${SUPABASE_ANON_KEY}
supabase.profilesTable=profiles

# --- Server ---
server.port=${PORT:8080}

# --- Actuator ---
management.endpoints.web.exposure.include=health,info

# --- JWT ---
supabase.jwtSecret=${SUPABASE_JWT_SECRET}

# --- Logs ---
logging.level.org.springframework.security=DEBUG

# --- CORS ---
app.cors.allowed-origins=${CORS_LIST}
```

### Variables de entorno necesarias

| Variable                    | Descripción                                                                |
| --------------------------- | -------------------------------------------------------------------------- |
| `SUPABASE_URL`              | URL del proyecto Supabase (p. ej. `https://abc123.supabase.co`)            |
| `SUPABASE_SERVICE_ROLE_KEY` | **Service Role Key** (clave admin, solo backend)                           |
| `SUPABASE_ANON_KEY`         | **Anon Key** (clave pública para flujos de login/refresh/user)             |
| `SUPABASE_JWT_SECRET`       | **JWT Secret** (para validar `access_token` HS256 en Spring Security)      |
| `PORT` (opcional)           | Puerto HTTP del servidor (por defecto `8080`)                              |
| `CORS_LIST` (opcional)      | Orígenes permitidos (CSV), p. ej. `http://localhost:3000,http://127.0.0.1` |

> **Nota:** Los tokens de Supabase en este proyecto están firmados con **HS256**. Por eso usamos `SUPABASE_JWT_SECRET` y un `JwtDecoder` HMAC en Spring Security.

### Ejemplo `.env` (desarrollo)

```env
SUPABASE_URL=https://abc123.supabase.co
SUPABASE_SERVICE_ROLE_KEY=eyJhbGciOiJIUzI1...
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1...
SUPABASE_JWT_SECRET=eyJhbGciOiJIUzI1...
CORS_LIST=http://localhost:3000
PORT=8080
```