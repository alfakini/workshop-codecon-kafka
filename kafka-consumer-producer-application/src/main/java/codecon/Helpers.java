package codecon;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Helpers {
    public static Properties loadProperties(String fileName) throws IOException {
        Properties props = new Properties();

        FileInputStream input = new FileInputStream(fileName);
        props.load(input);
        input.close();

        return props;
    }

    public static List<String> readEventsFile(String path) {
        try {
            List<String> linesToProduce = Files.readAllLines(Paths.get(path));

            return linesToProduce.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println(String.format("Error reading file %s due to %s", path, e));
        }

        return Collections.emptyList();
    }

    public static void printMetadata(final Collection<Future<RecordMetadata>> metadataList) {
        System.out.println("CODECON METADATA");

        metadataList.forEach(value -> {
            try {
                final RecordMetadata metadata = value.get();
                System.out.println(String.format(
                        "Record written to offset %s timestamp %s",
                        metadata.offset(),
                        metadata.timestamp()));
            } catch (InterruptedException | ExecutionException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
