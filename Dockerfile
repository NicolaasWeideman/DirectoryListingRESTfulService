FROM openjdk:8
ADD ./build/libs/DirectoryListingREST-0.1.0.jar ./build/libs/app.jar
ENTRYPOINT ["java", "-jar",  "./build/libs/app.jar"]

