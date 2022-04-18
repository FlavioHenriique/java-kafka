package consumer;
import messages.CorrelationId;
import messages.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.GsonSerializer;
import dispatcher.KafkaDispatcher;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupID, String topic, ConsumerFunction parse, Map<String, String> properties){
        this(groupID, parse, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupID, Pattern topic, ConsumerFunction parse, Map<String, String> properties) {
        this(groupID,parse, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(String groupID, ConsumerFunction parse, Map<String, String> properties) {
        this.parse = parse;
        consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupID, properties));
    }

    public void run() throws ExecutionException, InterruptedException {
        while (true){
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()){
                LOGGER.info("found " + records.count() + " records");
                for (var record: records){
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                        var message = record.value();
                        var deadLetter = new KafkaDispatcher<>();
                        deadLetter.send("LOJA_DEADLETTER", new GsonSerializer().serialize("", message),
                                message.getId().continueWith("deadLetter"));
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupID, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, groupID + "_" + UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() throws IOException {
        this.consumer.close();
    }
}
