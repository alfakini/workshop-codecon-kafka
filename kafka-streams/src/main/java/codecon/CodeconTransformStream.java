package codecon;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import codecon.avro.Event;
import codecon.avro.TransformedEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class CodeconTransformStream {
    private final Properties props;
    private final String inputTopic;
    private final Integer inputTopicPartitions;
    private final Short inputTopicReplicationFactor;
    private final String outputTopic;
    private final Integer outputTopicPartitions;
    private final Short outputTopicReplicationFactor;

    public CodeconTransformStream(Properties props) {
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

        // TODO Implementar

        client.close();
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
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put("default.deserialization.exception.handler", LogAndContinueExceptionHandler.class);

        new CodeconTransformStream(props).run();
    }

}
