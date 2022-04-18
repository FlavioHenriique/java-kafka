package consumer;

import messages.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {

    public void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
