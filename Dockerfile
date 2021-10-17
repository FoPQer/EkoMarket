FROM adoptopenjdk/openjdk11
ARG JAR_FILE=build/libs/Ecomarket-0.0.1-SNAPSHOT.jar
WORKDIR /d/Eco
COPY ${JAR_FILE} Eco.jar
COPY ./build/classes ./.
ENTRYPOINT ["java", "-jar", "Eco.jar"]