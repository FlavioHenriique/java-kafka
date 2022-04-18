package app;

import consumer.ConsumerService;
import consumer.ServiceRunner;
import io.IO;
import messages.Message;
import model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import consumer.KafkaService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportApp implements ConsumerService<User> {

    private static final String TOPIC = "LOJA_USER_GENERATE_READING_REPORT";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingReportApp.class);
    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(ReadingReportApp::new).start(5);
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportApp.class.getSimpleName();
    }

    @Override
    public void parse(ConsumerRecord<String, Message<User>> record) {
        LOGGER.info("processing report for " + record.value());
        var user = record.value();
        var target = new File(user.getPayload().getReportPath());
        try {
            IO.copyTo(SOURCE, target);
            IO.append(target, "Created for " + user.getPayload().getUuid());

            LOGGER.info("Created " + target.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}