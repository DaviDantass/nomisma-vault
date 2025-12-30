# ====================================
# STAGE 1: Build da aplicação
# ====================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Diretório de trabalho
WORKDIR /app

# Copia apenas os arquivos de dependências primeiro (aproveita o cache do Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixa as dependências (essa camada será cacheada se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Faz o build da aplicação (pula os testes para build mais rápido)
RUN mvn clean package -DskipTests

# ====================================
# STAGE 2: Imagem de runtime
# ====================================
FROM eclipse-temurin:21-jre-alpine

# Adiciona usuário não-root por segurança (boa prática!)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Diretório de trabalho
WORKDIR /app

# Copia apenas o JAR da aplicação do stage anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Configura JVM para containers (importante!)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
