#!/bin/bash

# 8080 AUTH
# 8081 CONTA
# 8082 GERENTE
# 8083 CLIENTE/USER
# 8084 SAGA

services=("shared" "conta" "gerente" "saga")

build_jar() {
    service=$1
    echo "Buildando microserviço $service"
    cd $service
    mvn clean install -DskipTests
    cd ..
}

for s in "${services[@]}"; do
    build_jar $s
    build_image $s
done

#user->manual (jar)
cd user-service/user
echo "Buildando microserviço user"
mvn clean install -DskipTests
cd .. && cd ..

#auth->manual (jar)
cd auth-service/auth
echo "Buildando microserviço auth"
mvn clean install -DskipTests
cd .. && cd ..

#docker
echo "Buildando imagem Docker para serviço conta"
docker build --pull --rm -f "conta/Dockerfile" -t conta "conta"

echo "Buildando imagem Docker para serviço gerente"
docker build --pull --rm -f "gerente/Dockerfile" -t gerente "gerente"

echo "Buildando imagem Docker para serviço saga"
docker build --pull --rm -f "saga/Dockerfile" -t saga "saga"

echo "Buildando imagem Docker para serviço cliente"
docker build --pull --rm -f "user-service/user/Dockerfile" -t cliente "user-service/user"

echo "Buildando imagem Docker para serviço auth"
docker build --pull --rm -f "auth-service/auth/Dockerfile" -t auth "auth-service/auth"

docker network create bantads
docker-compose -f compose.yaml up --build -d

#tempo para subir tudo
sleep 10

#up
docker-compose -f apps-compose.yaml up --build -d
