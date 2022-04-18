import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import messages.CorrelationId;
import model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.KafkaDispatcher;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OrderServlet extends HttpServlet {

    private static final String ORDER_TOPIC = "LOJA_NOVO_PEDIDO";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServlet.class);
    private  final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("creating producer");

        var email = req.getParameter("email");
        for(int i = 0; i < 10; i ++){
            try {
                var userId = "";
                var orderID = req.getParameter("uuid");
                var order = new Order(orderID, new BigDecimal(req.getParameter("amount")), email);

                var database = new OrdersDatabase();

                if (database.saveNew(order)){
                    orderDispatcher.send(ORDER_TOPIC, order, new CorrelationId(OrderServlet.class.getSimpleName()));
                    LOGGER.info("new order sent");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("new order sent");
                }else{
                    LOGGER.info("old order received");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("old order received");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}