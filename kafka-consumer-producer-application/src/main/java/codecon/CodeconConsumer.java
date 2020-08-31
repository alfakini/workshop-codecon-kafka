package codecon;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class CodeconConsumer {

  private volatile boolean keepConsuming = true;
  private Consumer<String, String> consumer;

  public CodeconConsumer(final Consumer<String, String> consumer) {
    this.consumer = consumer;
  }

  public void run() {
    try {
      while (keepConsuming) {
        final ConsumerRecords<String, String> events = consumer.poll(Duration.ofSeconds(1));
        events.forEach(event -> System.out.println(event));
      }
    } finally {
      consumer.close();
    }
  }

  public void shutdown() {
    keepConsuming = false;
  }


  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      throw new IllegalArgumentException(
          "This program takes one argument: the path to an environment configuration file.");
    }

    final Properties props = Helpers.loadProperties(args[0]);
    final String topic = props.getProperty("input.topic.name");

    final Consumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Arrays.asList(topic));

    final CodeconConsumer application = new CodeconConsumer(consumer);
    Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
    application.run();
  }
}
