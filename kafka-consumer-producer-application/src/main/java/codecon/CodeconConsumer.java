package codecon;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class CodeconConsumer {

    private final Consumer<String, String> consumer;
    private final String topic;
    private volatile boolean keepConsuming = true;

    public CodeconConsumer(Consumer<String, String> consumer, Properties props) {
        this.topic = props.getProperty("input.topic.name");
        this.consumer = consumer;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("This program takes one argument: " +
                    "the path to an environment configuration file.");
        }

        Properties props = Helpers.loadProperties(args[0]);

        CodeconConsumer application = new CodeconConsumer(new KafkaConsumer<>(props), props);
        Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));

        application.run();
    }

    public void run() {
        // TODO Implementar
    }

    public void shutdown() {
        keepConsuming = false;
    }
}
