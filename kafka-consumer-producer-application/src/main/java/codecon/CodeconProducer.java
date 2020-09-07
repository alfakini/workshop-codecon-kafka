package codecon;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class CodeconProducer {

    private final Producer<String, String> producer;
    private final String topic;

    public CodeconProducer(Producer<String, String> producer, Properties props) {
        this.producer = producer;
        this.topic = props.getProperty("output.topic.name");
    }

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "First argument is producer config file; second is the input data file.");
        }

        Properties props = Helpers.loadProperties(args[0]);

        CodeconProducer application = new CodeconProducer(new KafkaProducer<>(props), props);
        Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));

        List<String> events = Helpers.readEventsFile(args[1]);
        List<Future<RecordMetadata>> metadata = events.stream()
                .map(application::produce)
                .collect(Collectors.toList());

        Helpers.printMetadata(metadata);
    }

    public Future<RecordMetadata> produce(String event) {
        // TODO: Implementar
    }

    public void shutdown() {
        producer.close();
    }
}
