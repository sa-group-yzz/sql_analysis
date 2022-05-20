#!/usr/bin/env bash
set -e
mvn clean
echo "build binary..."
mvn package -DskipTests=true
echo "compile test cases..."
mvn test-compile