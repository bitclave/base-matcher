FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD build/libs/matcher*.jar /matcher.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/matcher.jar"]
