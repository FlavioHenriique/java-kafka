package consumer;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;

    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Void call() throws Exception {
        var myService = factory.create();

        KafkaService kafkaService = new KafkaService(
                myService.getConsumerGroup(),
                myService.getTopic(),
                myService::parse,
                Map.of());
        kafkaService.run();

        return null;
    }
}
