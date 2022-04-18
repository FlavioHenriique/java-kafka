package app;

import messages.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import consumer.KafkaService;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogConsumerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogConsumerApp.class);
    private static final String TOPIC = "LOJA.*";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        LogConsumerApp logConsumerApp = new LogConsumerApp();
        var service = new KafkaService(LogConsumerApp.class.getSimpleName(),
                Pattern.compile(TOPIC),
                logConsumerApp::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()));
        service.run();
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        LOGGER.info("LOG");
        LOGGER.info("TOPIC: " + record.topic());
        LOGGER.info("value: " + record.value());
        LOGGER.info("partition: " + record.partition());
        LOGGER.info("timestamp: " + record.timestamp());
    }
}
