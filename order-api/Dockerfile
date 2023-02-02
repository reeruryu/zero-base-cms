FROM openjdk:11-jre-slim
COPY build/libs/order-api.jar order-api.jacr
ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-Dmailgun.key=${MAILGUN_KEY}", "-jar", "order-api.jar"]