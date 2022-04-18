import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import messages.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dispatcher.KafkaDispatcher;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAllReportsServlet.class);
    private  final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();
    private static final String TOPIC = "LOJA_USER_GENERATE_READING_REPORT";
    private static final String TOPIC_SEND_MESSAGE = "LOJA_SEND_MESSAGE_TO_ALL_USERS";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("creating producer");
        try {
            batchDispatcher.send(
                    TOPIC_SEND_MESSAGE,
                    TOPIC,
                    new CorrelationId(GenerateAllReportsServlet.class.getSimpleName())
            );
            LOGGER.info("reports generated");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resp.setStatus(200);
        resp.getWriter().println("batch report send");
    }

    @Override
    public void destroy() {
        super.destroy();

    }
}
