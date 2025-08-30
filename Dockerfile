# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
# Copia monorepo para permitir compilar modulo dependente de contracts
COPY pom.xml ./
COPY contracts/pom.xml ./contracts/pom.xml
COPY contracts/src ./contracts/src
COPY sistema-vendas/pom.xml ./sistema-vendas/pom.xml
COPY sistema-vendas/src ./sistema-vendas/src
# Compila apenas os módulos necessários
RUN mvn -q -DskipTests -pl sistema-vendas -am package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/sistema-vendas/target/sistema-vendas-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
