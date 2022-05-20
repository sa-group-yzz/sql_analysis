FROM maven:3-openjdk-8

WORKDIR /sql
COPY pom.xml ./
COPY assertions ./assertions/
COPY src ./src/
COPY run_test.sh ./
COPY compile.sh ./

RUN mvn clean
RUN mvn package -DskipTests=true
RUN mvn test-compile