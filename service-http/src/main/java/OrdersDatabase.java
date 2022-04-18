import model.Order;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {

    private LocalDatabase database;

    public OrdersDatabase() {
        try {
            this.database = new LocalDatabase("order_database");
            database.createIfNotExists("create table Orders (uuid varchar(200))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveNew(Order order) throws SQLException {
        if (wasProcessed(order)){
            return false;
        }
        database.update("insert into orders (uuid) values (?)", order.getId());
        return true;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var result = database.query("select uuid from orders where uuid = ? limit 1" , order.getId());
        return result.next();
    }

    @Override
    public void close() throws IOException {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
