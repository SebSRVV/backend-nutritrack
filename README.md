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

## ⚙️ Configuración

Variables de entorno necesarias:

| Variable                  | Descripción                                      |
|----------------------------|--------------------------------------------------|
| `SUPABASE_URL`            | URL del proyecto Supabase (ej. `https://xyz.supabase.co`) |
| `SUPABASE_SERVICE_KEY`    | **service_role key** de Supabase (solo backend)   |

Ejemplo local (`application.properties` o env vars):

```properties
server.port=8080
supabase.url=${SUPABASE_URL}
supabase.service-key=${SUPABASE_SERVICE_KEY}
