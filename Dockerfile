##########################################################
# 1) STAGE DE BUILD (Maven + JDK 21)
##########################################################
FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app

# só copia o pom e resolve dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copia restante do código e empacota
COPY src ./src
RUN mvn clean package -DskipTests -B

##########################################################
# 2) STAGE DE RUNTIME (JRE 21 leve)
##########################################################
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# expõe porta HTTP
EXPOSE 8080

# copia o JAR construído
COPY --from=builder /app/target/challenge-softteck-0.0.1-SNAPSHOT.jar app.jar

# comando de inicialização
ENTRYPOINT ["java","-jar","/app/app.jar"]
