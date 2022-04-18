package dispatcher;

import messages.CorrelationId;
import messages.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDispatcher.class);

    public KafkaDispatcher(){
        this.producer = new KafkaProducer<String, Message<T>>(properties());
    }

    public Future<RecordMetadata> sendAsync(String topic, T payload, CorrelationId correlationId) throws ExecutionException, InterruptedException {
        LOGGER.info("sending message");

        var message = new Message<>(payload, correlationId.continueWith("_" + topic));
        var record = new ProducerRecord<String, Message<T>>(topic, message);
        Callback callback = (data, ex) -> {
            if (ex != null){
                LOGGER.info("Error: " + ex.getMessage());
            }else {
                LOGGER.info("topic " + data.topic() + ", partition " + data.partition() + ", offset "  + data.offset() + ", timestamp " + data.timestamp());
            }
        };
        return producer.send(record, callback);
    }

    public void send(String topic, T payload, CorrelationId correlationId) throws ExecutionException, InterruptedException {
        sendAsync(topic, payload, correlationId).get();
    }

    @Override
    public void close() throws IOException {
        this.producer.close();
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094,localhost:9093");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }
}
