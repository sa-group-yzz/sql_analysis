# sql_analysis

SQL analysis tool.


## Environment
1. Java 1.8
2. OS: Linux-Like systems such as Ubuntu and MacOS

## Docker image
以防万一无法提供环境或者在运行阶段出现任何无法正常运行的问题 ,我们提供了docker image，通过运行以下命令:
```bash
docker run -it --rm zzctmac/sql_analysis:v1 /bin/bash
```
即可获得我们配置好的环境 (当前前提是系统安装好docker)


## Run

### First step: Compile
```shell
./compile.sh
```

### Run one case
```shell
java -jar ./target/analysis-1.0-SNAPSHOT-jar-with-dependencies.jar -a ./assertions -t ./target/test-classes -c Case1
```

#### case folder
**./src/test/java/cases**

### Run all cases
```shell
./run_test.sh
```