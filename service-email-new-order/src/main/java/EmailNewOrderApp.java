import consumer.ConsumerService;
import consumer.KafkaService;
import consumer.ServiceRunner;
import dispatcher.KafkaDispatcher;
import messages.Message;
import model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderApp implements ConsumerService<Order> {

    private static final String TOPIC = "LOJA_NOVO_PEDIDO";
    private static final String EMAIL_TOPIC = "LOJA_NOVO_EMAIL";
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNewOrderApp.class);

    public static void main(String[] args){
        new ServiceRunner(EmailNewOrderApp::new).start(1);
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderApp.class.getSimpleName();
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        LOGGER.info("processing new order, preparing email");
        Order order = record.value().getPayload();
        var id = record.value().getId().continueWith(EmailNewOrderApp.class.getSimpleName());
        emailDispatcher.send(EMAIL_TOPIC, order.getEmail(), id);
    }
}
