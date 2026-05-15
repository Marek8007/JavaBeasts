FROM maven:3.9.9-eclipse-temurin-21

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libgl1 \
        libgtk-3-0 \
        libx11-6 \
        libxext6 \
        libxi6 \
        libxrender1 \
        libxtst6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src src
RUN mvn -q -DskipTests package dependency:copy-dependencies

ENV JAVABEASTS_LOBBY_HOST=backend
ENV JAVABEASTS_LOBBY_PORT=7878

CMD ["java", "--module-path", "target/dependency", "--add-modules", "javafx.controls", "-cp", "target/classes:target/dependency/*", "com.marcos.javabeasts_javafx.JavaBeastsLobbyApp"]
