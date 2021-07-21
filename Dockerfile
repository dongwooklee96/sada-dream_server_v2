FROM openjdk:11 AS builder

COPY . .

RUN ["./gradlew", "assemble"]

FROM openjdk:11

COPY --from=builder /build/libs/sada-dream_server_v2.jar .

CMD ["java", "-jar", "sada-dream_server_v2.jar"]
