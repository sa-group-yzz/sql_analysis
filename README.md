# README

## Environment

To build and run this project correctly, you need a Java 8 environment, and we use Maven as the build system.

1. Java 8
2. OS: We build and test our program on Linux systems such as Ubuntu and macOS. The program should work fine on Windows, but we didn't test it.

⚠️ Our programs can only be built and run on Java 8, we do not guarantee that any other Java version will work properly.

## Get the project

You can clone our project from Github:

```shell
git clone https://github.com/sa-group-yzz/sql_analysis.git
```



## Build

We offer a variety of ways to build the application.

### Docker

You can use Docker image to run our program, the advantage of using docker is that you do not need to configure the running environment. You need to install Docker, you can run the docker image by using the following command:

```shell
docker run -it --rm zzctmac/sql_analysis:v1 /bin/bash
```
Note that the first time you run the above command, docker needs to download this image since this image does not exist on your local computer. The time cost will be 1 to 10 minutes (depending on your Internet speed).

### Build from source code

#### 0: Optional Build the SQLAnalysis

> ⚠️ You can skip this step, we have provided a pre-compiled jar package in sql_analysis.

The SQLAnalysis is the SAND-NG module in our project, we already provide you a pre-compiled jar package in the sql_analysis git repository, but also if you want to build from the basement, you can build the SQLAnalysis.

First, you need to clone the SQLAnalysis project from Github:

```shell
git clone https://github.com/sa-group-yzz/SQLAnalysis.git
```

After that you can use Maven to build the SQLAnalysis package:

```shell
mvn -B package --file pom.xml
```

You can get the jar package in the target folder, the name of the jar package for the current edition SQLAnalysis is `SQLAnalysis-1.1.jar`. Then you can copy this package into the sa-group-yzz project. The path is `src/main/java/lib`.

#### 1: Build the sql_analysis

```shell
./compile.sh
```

## Run and test

### Run one test Java file

```shell
java -jar ./target/analysis-1.0-SNAPSHOT-jar-with-dependencies.jar -a ./assertions -t ./target/test-classes -c Case1
```

(Time: about 40s)

### Run all test Java files

```shell
./run_test.sh
```

(Time: about 10min)

