# Workshop: Event-driven architecture with Kafka

## Dependências

Para executar os exemplos de código desse repositório instale o OpenJDK 11 e
o Docker.

* [OpenJDK 11](https://adoptopenjdk.net/installation.html#x64_linux-jdk)
* [Docker](https://docs.docker.com/get-docker/)

## Editor de código

Para construir este tutorial usamos o [VSCode](https://code.visualstudio.com/) com os seguintes plugins:

* [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
* [avro-idl](https://marketplace.visualstudio.com/items?itemName=streetsidesoftware.avro)

## Configuração

```sh
git clone git@github.com:magrathealabs/workshop-codecon-kafka.git
cd workshop-codecon-kafka
```

## Executando serviços

Inicie os serviços executando no terminal:

```sh
docker-compose up -d
```

Para finalizar os serviços, vá para janelas abertas com instâncias de producers,
consumers ou shells e aperte `CTRL+C` e finalize os containers com `CTRL+D`.
Finalize então a stack de serviços executando:

```sh
docker-compose down
```

## Exemplos

0. [Introdução aos Streams, Producers e Consumers](./basic-shell-streams/README.md)
1. [Introdução ao Kafka](./kafka-introduction/README.md)
2. [Escrevendo Produtores e Consumidores](./kafka-consumer-producer-application/README.md)
3. [Usando Kafka Stream](./kafka-streams/README.md)

## Comandos básicos

* Para fechar o console do consumer e producer do Kafka use `CTRL+C`.
* Para fechar o shell do container do Docker use `CTRL+D`.
* Para iniciar uma sessão bash no broker do Kafka digite:

```sh
docker-compose exec broker bash
```
