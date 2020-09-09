# Kafka Streams

Neste tutorial vamos aprender a usar o [Avro](https://avro.apache.org/docs/current/) e
o [Kafka Streams](https://kafka.apache.org/documentation/streams/).

Avro é um sistema de serialização de dados que usa o formato JSON para definir schemas.
Usamos ele para trafegar em um formato binário dados serializados.

O Kafka suporta o tráfego de dados no formato chave-valor, onde o valor pode assumir
qualquer tipo primitivo. Usamos então o Avro para binarizar objetos mais comeplexos.
O Avro pode ainda ser usado para fazer chamadas RPC (Remote Procedure Calls).

Vamos falar sobre os seguintes arquivos:

* [src/main/avro/countsum.avsc](./src/main/avro/countsum.avsc)
* [src/main/avro/event.avsc](./src/main/avro/event.avsc)
* [src/main/avro/rating.avsc](./src/main/avro/rating.avsc)
* [src/main/avro/transformed_event.avsc](./src/main/avro/transformed_event.avsc)
* [src/main/java/codecon/CodeconAverageStream.java](./src/main/java/codecon/CodeconAverageStream.java)
* [src/main/java/codecon/CodeconTransformStream.java](./src/main/java/codecon/CodeconTransformStream.java)
* [src/main/java/codecon/Helpers.java](./src/main/java/codecon/Helpers.java)

## Antes de qualquer coisa

> O ideial é sempre que vamos comecar um novo exemplo fechar e iniciar novamente os
> serviços que descrevemos no `docker-compose.yml`. Não é obrigatório, mas por segurança
> para garantir que estamos começando um novo exemplo com o ambiente limpo.

## Transform Stream

Nosso objetivo é consumir objetos Avro no formato `{"id": 1, "title": "LGPD NA PRÁTICA::Lauro Gripa", "type": "palestra"}` a partir
do tópico `input_events`
e emitir objetos Avro no formato `{"id": 1, "title": "Lgpd Na Prática", "author":"Lauro Gripa", "type": "palestra"}` no tópico `transformed_events`.
Precisamos implementar `src/main/avro/transformed_event.avsc` e `src/main/java/codecon/CodeconTransformStream.java`.

Para compilar:

```sh
./gradlew shadowJarTransformStream
```

Execute a aplicação com o comando:

```sh
./gradlew runTransformStream
```

Para testas, execute o console do Producer com suporte ao Schema Avro:

```sh
docker exec -i schema-registry /usr/bin/kafka-avro-console-producer --topic input_events --broker-list broker:9092 --property value.schema="$(< src/main/avro/event.avsc)"
```

Envie alguns dados de teste no formato esperado:

```json
{"id": 1, "title": "ARQUITETURA EVENT-DRIVEN COM APACHE KAFKA::Alan R. Fachini", "type": "workshop"}
{"id": 2, "title": "OPEN SOURCE: PERCA O MEDO, FAÇA HOJE MESMO::Juliemar Berri", "type": "palestra"}
{"id": 3, "title": "LGPD NA PRÁTICA::Lauro Gripa", "type": "palestra"}
{"id": 4, "title": "APIS RÁPIDAS COM GRPC::Julio Monteiro", "type": "palestra"}
```

E veja eles saindo do outro lado no Consumer:

```sh
docker exec -it schema-registry /usr/bin/kafka-avro-console-consumer --topic transformed_events --bootstrap-server broker:9092 --from-beginning
```

## Average Stream

Nosso objetivo é consumir objetos Avro no formato `{"event_id": 1,"rating": 10}` a partir do tópico `ratings`
e emitir o valor da média dos ratings para um `event_id` no tópico `rating_averages` sem serialiação Avro. A chave
deve ser `Long` e o valor deve ser `Double`.
Precisamos implementar `src/main/java/codecon/CodeconAverageStream.java`.

Para implementar, precisamos fazer o seguinte:

1. Agrupar (group by) os ratings pela chave (`event_id`);
2. Calcular o total de um evento com mesmo `event_id` que chegou no stream, e a média de `rating` para esse `event_id`. Podemos fazer isso usando `.aggregate` e seriallizando os valores intermedirários em um objeto Avro `CountAndSum`.;
3. Materializar o objeto em uma `KTable` para guardar o estado local;
4. Calcular a média;
5. Publicar para um Stream.

Para compilar:

```sh
gradle shadowJarAverageStream
```

Para executar a aplicação:

```sh
gradle runAverageStream
```

Então abra um console do Consumer para o tópico `rating-average` onde a aplicação irá publicar os eventos:

```sh
docker exec -it broker /usr/bin/kafka-console-consumer --topic rating_averages --bootstrap-server broker:9092 \
  --property "print.key=true"\
  --property "key.deserializer=org.apache.kafka.common.serialization.LongDeserializer" \
  --property "value.deserializer=org.apache.kafka.common.serialization.DoubleDeserializer" \
  --from-beginning
```

E abra um console do Producer para enviar dados para o tópico `ratings` de onde a aplicação irá receber os eventos:

```sh
docker exec -i schema-registry /usr/bin/kafka-avro-console-producer --topic ratings --broker-list broker:9092\
  --property "parse.key=false"\
  --property "key.separator=:"\
  --property value.schema="$(< src/main/avro/rating.avsc)"
```

E envie alguns dados de exemplo:

```json
{"event_id": 1,"rating": 10}
{"event_id": 2,"rating": 5}
{"event_id": 3,"rating": 5}
{"event_id": 4,"rating": 9}

{"event_id": 1,"rating": 9}
{"event_id": 2,"rating": 4}
{"event_id": 3,"rating": 3}
{"event_id": 4,"rating": 7}
```
