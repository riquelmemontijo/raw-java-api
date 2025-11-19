# Estágio 1: Build com Maven
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o pom.xml para baixar as dependências (aproveitando o cache do Docker)
COPY pom.xml .

# Copia o restante do código-fonte
COPY src ./src

# Executa o build do Maven para baixar dependências e criar o JAR executável
RUN mvn -B -f pom.xml clean package -DskipTests

# Estágio 2: Imagem de Execução (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR executável do estágio de build para a imagem final
# O maven-shade-plugin gera o JAR original (sem dependências) e depois o sobrescreve com o shaded
COPY --from=build /app/target/raw-java-api-1.0-SNAPSHOT.jar app.jar

# Expõe a porta que a aplicação usa
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java", "-jar", "app.jar"]