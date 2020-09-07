# Escrevendo Producers e Consumers para o Kafka

Neste exemplo vamos escrever nossos próprios consumer e producer. Para isso, precisamos escrever
os seguintes arquivos:

* [configuration/consumer.properties](./configuration/consumer.properties)
* [configuration/producer.properties](./configuration/producer.properties)
* [src/main/java/codecon/CodeconConsumer.java](./src/main/java/codecon/CodeconConsumer.java)
* [src/main/java/codecon/CodeconProducer.java](./src/main/java/codecon/CodeconProducer.java)

## Antes de qualquer coisa

> O ideial é sempre que vamos comecar um novo exemplo fechar e iniciar novamente os
> serviços que descrevemos no `docker-compose.yml`. Não é obrigatório, mas por segurança
> para garantir que estamos começando um novo exemplo com o ambiente limpo.

## Inicializando o Kafka Broker

A primeira coisa que vamos fazer é criar um tópico para produzir e consumir
eventos. Use o seguinte comando:

```console
docker-compose exec broker kafka-topics --create --topic codecon-kafka-consumer-producer \
--bootstrap-server broker:9092 --replication-factor 1 --partitions 1
```

## Implementando o Consumer

Precisamos primeiro compilar o projeto e criar um arquivo jar executável para o consumer:

```console
gradle shadowJarConsumer
```

> [Shadow](https://imperceptiblethoughts.com/shadow/introduction/) is a Gradle plugin for combining a project's dependency classes and resources into a single output Jar. The combined Jar is often referred to a fat-jar or uber-jar.

Execute o consumer:

```console
java -jar build/libs/codecon-consumer.jar configuration/consumer.properties
```

> Passamos `configuration/consumer.properties` como parâmetro para injetar as
> configuradores necessárias para o consumer.

Agora abra um novo shell no Kafka Broker onde vamor iniciar um console do producer:

```console
docker-compose exec broker bash
```

Inicie o console do producer e envie dados para testar:

```broker-shell-producer
root@broker:/# kafka-console-producer --topic codecon-kafka-consumer-producer --broker-list broker:9092
```

## Implementando o Producer

Precisamos compilar o projeto e criar um arquivo jar executável para o producer:

```console
gradle shadowJarProducer
```

Execute o producer:

```console
java -jar build/libs/codecon-producer.jar configuration/producer.properties configuration/events.txt
```
