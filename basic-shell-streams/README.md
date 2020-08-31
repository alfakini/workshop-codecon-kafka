# Basic Shell Streams

> In general, a "stream" refers to data that is incrementally made available over time. The concept
> appears in many places: in the stdin and stdout of Unix, programming languages (lazy lists),
> filesystem APIs (such as Java’s FileInputStream), TCP connections, delivering audio and video over
> the internet, and so on. – Designing Data-Intensive Applications (Martin Kleppmann)

Em um contexto de stream processing, um registro (record) é comumente conhecido como um
evento (event), mas é essencialmente a mesma coisa: um objeto pequeno, independente e imutável contendo
os detalhes de algo que aconteceu em algum momento no tempo. Um evento geralmente contém um timestamp
indicando quando aconteceu.

Na terminologia de streaming, um evento é gerado por um produtor (producer, publisher ou sender) e,
em seguida, potencialmente processado por vários consumidores (consumers, sbscribers ou recipients).

Em um sistema de arquivos, um nome de arquivo identifica um conjunto de registros relacionados.
Em um sistema de streaming, eventos relacionado geralmente são agrupados em um tópico ou fluxo.

Em princípio, um arquivo ou banco de dados é suficiente para conectar producers e consumers: um producer escreve
cada evento que ele gera para o datastore e cada consumer periodicamente lê os eventos.

## Testando essa ideia

Com base nesse conceito, podemos então facilmente testar a ideia de um sistema de streaming de eventos
usando ferramentas disponíveis em qualquer sistema UNIX.

Vamos primeiro criar nosso consumer:

```sh
touch events.txt
tail -f events.txt
```

Com esses dois comandos nós criamos o arquivo `events.txt` e criamos um output de dados que fica "pendurado" (attached).

Agora vamos criar o nosso producer:

```sh
echo "isso é um evento" >> events.txt
```

Repare aqui que nós usamos o símbolo `>>` para criar uma nova linha no arquivo.

Podemos ainda simular algo muito parecido com o que sistemas AMQ como o RabbitMQ fazem, usando o comando `nc` do UNIX.

Criando um producer:

```sh
nc -l localhost 3000
```

E um consumer:

```sh
nc localhost 3000
```

Netcat é uma ferramenta de linha de comando que abre um socket onde podemos escrever e ler dados.

## E o Kafka?

_Extraído do livro Designing Data-Intensive Applications (Martin Kleppmann)._

Uma arquitetura diferente para esse tipo de sistema é enviar os eventos por meio de um Message Broker (ou Message Queue),
que é essencialmente um tipo de banco de dados otimizado para lidar com fluxos de mensagens. Ele funciona como um servidor,
com producers e consumers conectando-se a ele como clientes. Os producers escrevem mensagens para o Broker,
e os consumers os recebem.

Ao centralizar os dados no Broker, esses sistemas podem tolerar mais facilmente falhas e o problema de durabilidade
dos dados fica como responsabilidade do Broker. Alguns Brokers apenas mantêm mensagens na memória, enquanto outros,
como é o caso do Kafka, gravam os dados em disco para que não sejam perdidos em caso de falha. Diante de consumers lentos, eles
geralmente permitem enfileiramento ilimitado.

Outra vantagem que ganhamos quando usamos Brokers é que os consumers são geralmente assíncronos: quando um producer envia
uma mensagem, normalmente só espera que o Brokers confirme que armazenou a mensagem em buffer (recebe um ack) e
não espera que a mensagem seja processada pelos consumers. A entrega aos consumers acontecerá em algum ponto futuro
indeterminado no tempo, muitas vezes dentro de uma fração de segundo, mas às vezes significativamente mais tarde,
se houver um backlog da fila.
