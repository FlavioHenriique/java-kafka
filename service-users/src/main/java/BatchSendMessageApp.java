import messages.CorrelationId;
import messages.Message;
import model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.KafkaDispatcher;
import consumer.KafkaService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSendMessageApp.class);
    private final Connection connection;
    private static final String TOPIC = "LOJA_SEND_MESSAGE_TO_ALL_USERS";
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    BatchSendMessageApp() throws SQLException {
        String url = "jdbc:sqlite:target/user_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table users (uuid varchar(200), email varchar(200))");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        BatchSendMessageApp app = new BatchSendMessageApp();
        var kafkaService = new KafkaService<String>(
                BatchSendMessageApp.class.getSimpleName(),
                TOPIC,
                app::parse,
                Map.of());
        kafkaService.run();
    }

    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        LOGGER.info("processing new batch");
        var message = record.value();

        if (true) throw new RuntimeException("erro forçadasso");
        for (User user: getAllUsers()){
            userDispatcher.sendAsync(
                    message.getPayload(),
                    user,
                    message.getId().continueWith(BatchSendMessageApp.class.getSimpleName())
            );
            LOGGER.info("Acho que enviei para " + user);
        }
    }

    private List<User> getAllUsers() throws SQLException {
        var result = connection.prepareStatement("select uuid from users").executeQuery();
        List<User> users = new ArrayList<>();
        while (result.next()){
            users.add(new User(result.getString("uuid")));
        }
        return users;
    }
}
