package codecon;

import java.util.concurrent.Future;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class CodeconProducer {

    private final Producer<String, String> producer;
    final String topic;

    public CodeconProducer(final Producer<String, String> producer, final String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public Future<RecordMetadata> produce(final String event) {
        final ProducerRecord<String, String> producerRecord = createProducerRecord(topic, event);
        return producer.send(producerRecord);
    }

    public ProducerRecord<String, String> createProducerRecord(final String topic, final String event) {
        final String[] parts = event.split(":");
        final String key;
        final String value;

        if (parts.length > 1) {
            key = parts[0];
            value = parts[1];
        } else {
            key = "null";
            value = parts[0];
        }

        return new ProducerRecord<>(topic, key, value);
    }

    public void shutdown() {
        producer.close();
    }

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                "First argument is producer config file; second is the input data file.");
        }

        final Properties props = Helpers.loadProperties(args[0]);
        final String topic = props.getProperty("output.topic.name");

        final Producer<String, String> producer = new KafkaProducer<>(props);
        final CodeconProducer application = new CodeconProducer(producer, topic);
        Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));

        final List<String> events = Helpers.readEventsFile(args[1]);
        final List<Future<RecordMetadata>> metadata = events.stream()
          .map(application::produce)
          .collect(Collectors.toList());

        Helpers.printMetadata(metadata);
    }
}
