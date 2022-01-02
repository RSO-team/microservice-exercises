FROM adoptopenjdk:15-jre-hotspot

RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.0.0.jar /app

EXPOSE 8088

CMD ["java", "-jar", "api-1.0.0.jar"]
