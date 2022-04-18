package consumer;

import messages.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public interface ConsumerService<T> {

    public String getTopic();
    public String getConsumerGroup();
    public void parse(ConsumerRecord<String, Message<T>> record) throws Exception;
}
