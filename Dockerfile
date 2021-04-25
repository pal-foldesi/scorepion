FROM gradle:7.0.0-jdk16 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon --info

FROM adoptopenjdk:16_36-jre-hotspot
RUN addgroup runners && adduser runner && adduser runner runners
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENV DB_PASSWORD=DzCBTW9bmx26s0L8D0oOepW4iix8RE ADMIN_USERNAME=admin1 ADMIN_PASSWORD=h7oXY4pEWl6lNRdvbtEnoZEJgHtxjC JWT_SECRET=KfA3ANyw0oW5hg3Ae37PbeogwZQA5n2crLHt9xcWAtpocXlfePMNxjgfDPtZUsZ8KCJTNbF693gMhxOypH1xJz5B5Om43GkP9lHC
RUN chown -R runner:runners /app
USER runner
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]