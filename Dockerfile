FROM amazoncorretto:17

COPY target/kanga-learn.jar /
EXPOSE 8085
WORKDIR /
RUN echo $PATH
CMD ["java", "-Dfile.encoding=UTF-8", "-jar", "kanga-learn.jar"]
