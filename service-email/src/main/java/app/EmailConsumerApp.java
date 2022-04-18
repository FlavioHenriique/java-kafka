package app;
import consumer.ConsumerService;
import consumer.ServiceRunner;
import messages.Message;
import model.Email;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmailConsumerApp implements ConsumerService<String> {
    private static final String TOPIC = "LOJA_NOVO_EMAIL";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumerApp.class);

    public static void main(String[] args) throws Exception {
        new ServiceRunner(EmailConsumerApp::new).start(5);
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return EmailConsumerApp.class.getSimpleName();
    }


    public void parse(ConsumerRecord<String, Message<String>> record){
        LOGGER.info("sending the email");
        LOGGER.info("key: " + record.key());
        LOGGER.info("value: " + record.value());
        LOGGER.info("partition: " + record.partition());
        LOGGER.info("timestamp: " + record.timestamp());
        LOGGER.info("email sent");
    }


}
