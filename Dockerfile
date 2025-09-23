# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
# Build sin tests (ajusta si quieres correrlos en CI)
RUN ./mvnw -q -DskipTests clean package

# ---- runtime stage ----
FROM eclipse-temurin:21-jre
# Crea usuario no-root
RUN useradd -ms /bin/bash appuser
WORKDIR /app

# Render expone PORT; Spring Boot lee SERVER_PORT si lo seteas
ENV SERVER_PORT=${PORT}
# Flags JVM seguros en contenedores
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

# Copia el JAR (ajusta el nombre si cambia)
COPY --from=build /app/target/supabase-backend-0.0.1-SNAPSHOT.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
  CMD wget -qO- http://127.0.0.1:${SERVER_PORT:-8080}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
