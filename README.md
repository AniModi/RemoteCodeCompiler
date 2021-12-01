[![Build and Test](https://github.com/zakariamaaraki/RemoteCodeCompiler/actions/workflows/build.yaml/badge.svg)](https://github.com/zakariamaaraki/RemoteCodeCompiler/actions/workflows/build.yaml) [![Docker](https://badgen.net/badge/icon/docker?icon=docker&label)](https://https://docker.com/) [![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/) [![Test Coverage](https://github.com/zakariamaaraki/RemoteCodeCompiler/blob/master/.github/badges/jacoco.svg)](https://github.com/zakariamaaraki/RemoteCodeCompiler/actions/workflows/build.yaml)
[![CodeQL](https://github.com/zakariamaaraki/RemoteCodeCompiler/actions/workflows/codeAnalysis.yaml/badge.svg)](https://github.com/zakariamaaraki/RemoteCodeCompiler/actions/workflows/codeAnalysis.yaml)

# Remote Code Compiler

An online code compiler for Java, C, C++ and Python for competitive programming and coding interviews.
This service execute your code remotely using docker containers to separate the different environements of executions.

Support **Rest Calls** and **Apache Kafka Messages**.

## Prerequisites

To run this project you need a docker engine running on your machine.

## Getting Started

Build docker image by typing the following command :

```shell
docker image build . -t compiler
```

Run the container by typing the following command

```shell
sudo docker container run -p 8080:8082 -v /var/run/docker.sock:/var/run/docker.sock -e DELETE_DOCKER_IMAGE=true -e EXECUTION_MEMORY_MAX=10000 -e EXECUTION_MEMORY_MIN=0 -e EXECUTION_TIME_MAX=15 -e EXECUTION_TIME_MIN=0 -t compiler
```
* The value of the env variable **DELETE_DOCKER_IMAGE** is by default set to true, and that means that each docker image is deleted after the execution of the container. 
* The value of the env variable **EXECUTION_MEMORY_MAX** is by default set to 10 000 MB, and represents the maximum value of memory limit that we can pass in the request. **EXECUTION_MEMORY_MIN** is by default set to 0.
* The value of the env variable **EXECUTION_TIME_MAX** is by default set to 15 sec, and represents the maximum value of time limit that we can pass in the request. **EXECUTION_TIME_MIN** is by default set to 0.  

### Portainer
It might be a good idea if you run a **Portainer** instance and mount it to the same volume, in order to have a total view of created images and running containers.

```shell
docker container run -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer
```

### On K8s
You can use the provided helm chart to deploy the project on k8s

```shell
helm install compiler ./k8s/helm_charts/compiler_chart
```

Note if you are running k8s using Minikube :
* you can reuse the Docker daemon from Minikube by running this command : **eval $(minikube docker-env)**.
* set image pull policy to **Never** in the values.yml file.


## How It Works

![Architecture](images/compiler.png?raw=true "Compiler")

There is four endpoints, one for Java, one for C, one for C ++ and another for Python. The call is done through POST requests to the following urls :

- localhost:8080/compiler/**java**
- localhost:8080/compiler/**c**
- localhost:8080/compiler/**cpp**
- localhost:8080/compiler/**python**

For the documentation visit the swagger page at the following url : http://localhost:8080/swagger-ui.html

![Compilers endpoints](images/swagger.png?raw=true "Swagger")

### Verdicts

* Accepted
* Wrong Answer
* Compilation Error
* Runtime Error
* Time Limit Exceeded
* Memory Limit Exceeded

### Visualize Docker images and containers info
It is also possible to visualize information about the images and docker containers that are currently running using these endpoints 

![Docker info](images/swagger-docker-infos.png?raw=true "Docker info Swagger")

#### Example of an Output

##### Docker Containers 

![Docker info response](images/docker-info-response.png?raw=true "Docker info Swagger")

##### Docker Images 

![Docker images info](images/docker-images-info.png?raw=true "Docker images info Swagger")

##### Docker Stats (Memory and CPU usage)

![Docker stats](images/docker-all-stats.png?raw=true "Docker stats")


### How the docker image is generated

We generate an entrypoint.sh file depending on the information given by the user (time limit, memory limit, programming language, and also the inputs).

![Alt text](images/image_generation.png?raw=true "Docker image Generation")

## Kafka Mode
You can use the compiler with an event driven architecture.
To enable kafka mode you can pass to the container the following env variables :
* **ENABLE_KAFKA_MODE** : true or false
* **KAFKA_INPUT_TOPIC** : input topic json request
* **KAFKA_OUTPUT_TOPIC** : output topic json response
* **KAFKA_CONSUMER_GROUP_ID** : consumer group
* **KAFKA_HOSTS** : list of brokers

```shell
sudo docker container run -p 8080:8082 -v /var/run/docker.sock:/var/run/docker.sock -e DELETE_DOCKER_IMAGE=true -e EXECUTION_MEMORY_MAX=10000 -e EXECUTION_MEMORY_MIN=0 -e EXECUTION_TIME_MAX=15 -e EXECUTION_TIME_MIN=0 -e ENABLE_KAFKA_MODE=true -e KAFKA_INPUT_TOPIC=topic.input -e KAFKA_OUTPUT_TOPIC=topic.output -e KAFKA_CONSUMER_GROUP_ID=compilerId -e KAFKA_HOSTS=ip_broker1,ip_broker2,ip_broker3 -t compiler
```

![remote code compiler kafka mode](images/compiler-kafka-mode.png?raw=true "Compiler Kafka Mode")

### Metrics
Check out exposed prometheus metrics using the following url : http://localhost:8080/actuator/prometheus

## Author

- **Zakaria Maaraki** - _Initial work_ - [zakariamaaraki](https://github.com/zakariamaaraki)
