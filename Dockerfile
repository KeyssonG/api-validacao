# Etapa 1: Build da aplicação Java
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas arquivos necessários para build (melhora cache do Docker)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Build otimizado com flags para reduzir tamanho
RUN mvn clean package -DskipTests \
    -Dmaven.compiler.release=21 \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true

# Etapa 2: Imagem final otimizada usando distroless (muito mais leve que alpine completo)
FROM gcr.io/distroless/java21-debian12:nonroot

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o JAR (sem dependências do Maven)
COPY --from=builder /app/target/*.jar /app/validacao.jar

# Usuário não-root para segurança
USER nonroot:nonroot

# Expõe a porta da aplicação
EXPOSE 8087

# JVM otimizada para baixo consumo de memória e CPU
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:MinRAMPercentage=50.0", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-XX:+UseStringDeduplication", \
    "-XX:+OptimizeStringConcat", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.jmx.enabled=false", \
    "-jar", "/app/validacao.jar"]
