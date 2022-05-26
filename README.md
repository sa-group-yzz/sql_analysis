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
在命令行进入当前项目目录
### First step: Compile
```shell
./compile.sh
```

### Run one test Java file
```shell
java -jar ./target/analysis-1.0-SNAPSHOT-jar-with-dependencies.jar -a ./assertions -t ./target/test-classes -c Case1
```
(时间：40s左右)

### Run all test Java files
```shell
./run_test.sh
```
(时间：10min左右)