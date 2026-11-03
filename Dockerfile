# =====================================================================
# Etapa 1: compilación
# =====================================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Cache de dependencias: solo se invalida si cambia el pom.xml
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# =====================================================================
# Etapa 2: runtime
# =====================================================================
FROM eclipse-temurin:17-jre-jammy

# Fuentes para que JasperReports pueda exportar los PDF (DejaVu Sans)
RUN apt-get update \
    && apt-get install -y --no-install-recommends fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

ENV TZ=America/Argentina/Buenos_Aires \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    PORT=8091 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -XX:+ExitOnOutOfMemoryError -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Duser.timezone=America/Argentina/Buenos_Aires"

# Usuario sin privilegios
RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app

WORKDIR /app
COPY --from=build /build/target/*.jar /app/app.jar
RUN chown -R app:app /app

USER app

EXPOSE 8091

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
