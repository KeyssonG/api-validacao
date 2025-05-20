# Etapa 1: Build da aplicação Java
FROM maven:3.9.9-amazoncorretto-21 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia todo o conteúdo do projeto (inclusive pom.xml e src)
COPY . .

# Executa o build do projeto
RUN mvn clean package -DskipTests

# Renomeia o JAR gerado
RUN mv target/*.jar /app/validacao.jar

# Etapa 2: Imagem final para execução
FROM amazoncorretto:21

WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=builder /app/validacao.jar /app/validacao.jar

# Expõe a porta da aplicação
EXPOSE 8083

# Comando de inicialização
CMD ["java", "-jar", "/app/validacao.jar"]
