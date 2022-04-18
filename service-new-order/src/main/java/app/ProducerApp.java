package app;

import messages.CorrelationId;
import model.Email;
import model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.KafkaDispatcher;


import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProducerApp {
    private static final String ORDER_TOPIC = "LOJA_NOVO_PEDIDO";
    private static final String EMAIL_TOPIC = "LOJA_NOVO_EMAIL";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LOGGER.info("creating producer");
        var orderDispatcher = new KafkaDispatcher<Order>();
        var emailDispatcher = new KafkaDispatcher<Email>();
        var email = Math.random() + "@gmail.com";
        for(int i = 0; i < 10; i ++){

            var userId = "";
            var orderID = UUID.randomUUID().toString();
            var order = new Order(i, orderID, new BigDecimal(Math.random() * 5000 + 1), email);

            orderDispatcher.send(ORDER_TOPIC, order, new CorrelationId(ProducerApp.class.getSimpleName()));
        }
        LOGGER.info("end");
    }
}
