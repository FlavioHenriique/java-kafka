import consumer.ConsumerService;
import consumer.ServiceRunner;
import messages.Message;
import model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.KafkaDispatcher;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class OrderConsumerApp implements ConsumerService<Order> {

    private static final String TOPIC = "LOJA_NOVO_PEDIDO";
    private static final String FRAUD_TOPIC = "LOJA_NOVO_PEDIDO_REJEITADO";
    private static final String APPROVED_TOPIC = "LOJA_NOVO_PEDIDO_APROVADO";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumerApp.class);
    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();
    private final LocalDatabase database;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(OrderConsumerApp::new).start(1);

    }

    public OrderConsumerApp() throws SQLException {
        this.database = new LocalDatabase("fraud_database");
        database.createIfNotExists("create table Orders (uuid varchar(200), is_fraud boolean)");
    }


    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return OrderConsumerApp.class.getSimpleName();
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        LOGGER.info("processing new order");
        var order = record.value().getPayload();
        var message = record.value();
        LOGGER.info(order.toString());

        if (wasProcessed(order)){
            LOGGER.info("Order already processed");
            return;
        }

        if (isFraud(order)){
            LOGGER.info("Order is a fraud !!!");
            database.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getId());
            dispatcher.send(
                    FRAUD_TOPIC,
                    order,
                    message.getId().continueWith(OrderConsumerApp.class.getSimpleName())
            );
        }else {
            LOGGER.info("Order aproved.");
            database.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getId());
            dispatcher.send(
                    APPROVED_TOPIC,
                    order,
                    message.getId().continueWith(OrderConsumerApp.class.getSimpleName())
            );
        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var result = database.query("select uuid from orders where uuid = ? limit 1" , order.getId());
        return result.next();
    }
}
