package consumer;

import java.sql.SQLException;

public interface ServiceFactory<T> {

    public ConsumerService<T> create() throws Exception;
}
