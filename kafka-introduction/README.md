# Introdução ao Kafka

A primeira coisa que vamos fazer é criar um tópico para produzir e consumir
eventos. Use o seguinte comando:

```console
docker-compose exec broker kafka-topics --create --topic codecon-kafka-intro --bootstrap-server broker:9092 --replication-factor 1 --partitions 1
```

A seguir, vamos iniciar um consumidor para ler os eventos enviados. Execute o
seguinte comando para iniciar uma sessão bash no container do Kafka Broker:

```console
docker-compose exec broker bash
```

De dentro do container, inicialize o console do consumer:

```broker-shell-consumer
root@broker:/# kafka-console-consumer --topic codecon-kafka-intro --bootstrap-server broker:9092
```

O consumidor irá iniciar, mas não veremos nada até enviar eventos.

Para produzir um primeiro evento no Kafka, abra outro terminal para iniciar uma
segunda sessão bash no container do Kafka Broker. De dentro do segundo shell,
execute o seguinte comando para iniciar o console do producer:

```broker-shell-producer
root@broker:/# kafka-console-producer --topic codecon-kafka-intro --broker-list broker:9092
```

Cada linha enviada a partir desse console representa um evento enviado.
Digite uma linha por vez, pressione `Enter` e volte para a janela do consumer
para visualizar sua mensagem chegando.

Para fechar o console do consumer digite `CTRL+C`. Não feche ainda o do producer.

## Ler todos os eventos de uma vez

No exemplo acima vimos todos os eventos chegando no consumer porque abrimos um
console que aguardava todas as mensagens chegarem. Pode acontecer de termos
um tópico com muitos eventos e um consumidor ser iniciado algum tempo depois.
Nesse caso, ao iniciar o console do consumer, não viriamos os eventos, porque
por padrão os consumers só leem os eventos criados após sua inicialização. Se
quisermos ver todos os eventos já criados, precisamos adicionar a propriedade
`--from-beginning` ao comando que abre o console do consumer.

Neste exemplo, vá para o console do producer e envie mais alguns eventos:

```kafka-producer-console
> microservices
> kafka
> event-driven
> event-sourcing
> cqrs
> confluent
> ksqldb
```

Em seguida, abra o console do consumer usando a propriedade `--from-beginning`:

```broker-shell-consumer
root@broker:/# kafka-console-consumer --topic codecon-kafka-intro --bootstrap-server broker:9092  --from-beginning
```

Após a inicialização do consumer, você verá a saída dos eventos.

> A propriedade `--from-beginning` vai ler todos os eventos presentes no tópico. Tome cuidado ao usar essa propriedade em produção!

Feche o console do consumer e do producer.

## Producer com pares key:value

No Kafka sempre estamos trabalhando com pares de chave e valor, mesmo quando não
enviamos a chave. Nesses casos, a chave fica com o valor `Null`. Se quisermos
enviar eventos com chaves explícitas, precisamos adicionar as propriedades
`parse.key=true` e `key.separator=":"`:

```broker-shell-producer
root@broker:/# kafka-console-producer --topic codecon-kafka-intro --broker-list broker:9092\
  --property parse.key=true\
  --property key.separator=":"
```

Em seguida, envie os eventos usando o padrão de separador indicado na propriedade `key.separator`:

```kafka-producer-console
> codecon:maior evento tech norte catarinense
> codecon:a dev couch conference
> workshops:Arquitetura event-driven com apache kafka
> workshops:Introdução ao cypress
> workshops:Flutter - 3 horas de handson
```

Execute o comando a seguir para iniciar um console de consumer preparado apra ler
eventos no formato chave:valor. Para fazer isso, vamos usar as propriedades
`print.key=true` e key.separator=":":

```broker-shell-consumer
root@broker:/# kafka-console-consumer --topic codecon-kafka-intro --bootstrap-server broker:9092 \
 --from-beginning \
 --property print.key=true \
 --property key.separator=":"
```

Depois que o consumidor iniciar, você verá a seguinte saída:

```kafka-consumer-console
null:microservices
null:kafka
null:event-driven
null:event-sourcing
null:cqrs
null:confluent
null:ksqldb
codecon:maior evento tech do norte catarinense
codecon:a dev couch conference
workshops:Arquitetura event-driven com apache kafka
workshops:Introdução ao cypress
workshops:Flutter - 3 horas de handson
```
