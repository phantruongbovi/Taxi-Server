FROM gradle:7.1.1-jdk8
WORKDIR /app
COPY gradle ./build.gradle ./dump.rdb ./gradlew ./gradlew.bat ./settings.gradle ./
WORKDIR /app/src
COPY src ./
WORKDIR /app

USER root                
RUN chown -R gradle /app 
USER gradle
RUN gradle wrapper --gradle-version 7.1.1
RUN ./gradlew installDist
CMD ["./build/install/taixiWithRedis/bin/taxi-service"]
