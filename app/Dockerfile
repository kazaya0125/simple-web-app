##ビルドステージ##
FROM gradle:9-jdk17 AS build
WORKDIR /work
COPY . .
RUN ./gradlew build

##実行ステージ##
FROM tomcat:11.0.24-jdk17

COPY --from=build work/app/build/libs/app.war /usr/local/tomcat/webapps/app.war

EXPOSE 8080