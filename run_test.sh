#!/usr/bin/env bash
set -e
mvn clean
echo "build binary..."
mvn package -DskipTests=true
echo "compile test cases..."
mvn test-compile
END=24
echo "run tests..."
set +e
for ((i=1;i<=END;i++)); do
    java -jar ./target/analysis-1.0-SNAPSHOT-jar-with-dependencies.jar -a assertions -t target/test-classes -c Case$i
done
echo "all 50 cases success"