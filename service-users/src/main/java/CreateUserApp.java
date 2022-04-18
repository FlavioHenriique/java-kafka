import consumer.ConsumerService;
import consumer.ServiceProvider;
import consumer.ServiceRunner;
import messages.Message;
import model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import consumer.KafkaService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserApp implements ConsumerService<Order> {

    private static final String TOPIC = "LOJA_NOVO_PEDIDO";
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserApp.class);
    private final LocalDatabase database;

    public CreateUserApp() throws SQLException {
        this.database = new LocalDatabase("user_database");
        this.database.createIfNotExists("create table users (uuid varchar(200), email varchar(200))");
    }

    public static void main(String[] args) {
        new ServiceRunner(CreateUserApp::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception{
        LOGGER.info("processing new order, checking new user");
        var order = record.value().getPayload();
        if (isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }else {
            LOGGER.info("Is not a new user");
        }

    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        database.update("insert into users (uuid, email) values (?,?)", uuid, email);
        LOGGER.info("user uuid and " + email + " added");
    }

    private boolean isNewUser(String email) throws SQLException {
        var result = database.query("select uuid from users where email = ? limit 1", email);
        return !result.next();
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserApp.class.getSimpleName();
    }
}
