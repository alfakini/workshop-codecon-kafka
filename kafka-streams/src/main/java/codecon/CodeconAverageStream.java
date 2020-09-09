package codecon;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import codecon.avro.CountAndSum;
import codecon.avro.Rating;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.kafka.common.serialization.Serdes.Double;
import static org.apache.kafka.common.serialization.Serdes.Long;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.kstream.Grouped.with;

public class CodeconAverageStream {
    private final Properties props;
    private final String inputTopic;
    private final Integer inputTopicPartitions;
    private final Short inputTopicReplicationFactor;
    private final String outputTopic;
    private final Integer outputTopicPartitions;
    private final Short outputTopicReplicationFactor;

    public CodeconAverageStream(Properties props) {
        this.props = props;
        this.inputTopic = props.getProperty("input.topic.name");
        this.inputTopicPartitions = Integer.parseInt(props.getProperty("input.topic.partitions"));
        this.inputTopicReplicationFactor = Short.parseShort(props.getProperty("input.topic.replication.factor"));
        this.outputTopic = props.getProperty("output.topic.name");
        this.outputTopicPartitions = Integer.parseInt(props.getProperty("output.topic.partitions"));
        this.outputTopicReplicationFactor = Short.parseShort(props.getProperty("output.topic.replication.factor"));
    }

    public void createTopics() {
        AdminClient client = AdminClient.create(props);
        client.createTopics(List.of(
            new NewTopic(inputTopic, inputTopicPartitions, inputTopicReplicationFactor),
            new NewTopic(outputTopic, outputTopicPartitions, outputTopicReplicationFactor)
        ));
        client.close();
    }


    public static SpecificAvroSerde<CountAndSum> buildCountAndSumAvroSerde(Properties props) {
        Map<String, String> config = Map.of(
            SCHEMA_REGISTRY_URL_CONFIG, props.getProperty("schema.registry.url")
        );

        SpecificAvroSerde<CountAndSum> avro = new SpecificAvroSerde<>();
        avro.configure(config, false);
        return avro;
    }

    public static SpecificAvroSerde<Rating> buildRatingAvroSerde(Properties props) {
        Map<String, String> config = Map.of(
            SCHEMA_REGISTRY_URL_CONFIG, props.getProperty("schema.registry.url")
        );

        SpecificAvroSerde<Rating> avro = new SpecificAvroSerde<>();
        avro.configure(config, false);
        return avro;
    }


    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // TODO Implementar

        return builder.build();
    }

  private void run() {
    createTopics();

    KafkaStreams streams = new KafkaStreams(buildTopology(), props);

    CountDownLatch latch = new CountDownLatch(1);

    // Attach shutdown handler to catch Control-C.
    Runtime.getRuntime().addShutdownHook(new Thread("shutdown-hook") {
        @Override
        public void run() {
            streams.close();
            latch.countDown();
        }
    });

    try {
        streams.cleanUp();
        streams.start();
        latch.await();
    } catch (Throwable e) {
        System.exit(1);
    }

    System.exit(0);
  }


  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
        throw new IllegalArgumentException("This program takes the path to an environment configuration file as argument.");
    }

    Properties props = Helpers.loadProperties(args[0]);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Long().getClass());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Double().getClass());
    props.put("default.deserialization.exception.handler", LogAndContinueExceptionHandler.class);

    new CodeconAverageStream(props).run();
  }
}
