FROM amazoncorretto:21.0.4-alpine3.18

WORKDIR /app

COPY target/HotelApplication-0.0.1-SNAPSHOT.jar hotel-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "hotel-app.jar"]