import javax.xml.transform.Result;
import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    public void createIfNotExists(String sql){
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String statement, String ... params) throws SQLException {
        prepare(statement, params).execute();
    }

    public ResultSet query(String query, String ... params) throws SQLException {
        return prepare(query, params).executeQuery();
    }

    private PreparedStatement prepare(String query, String ... params) throws SQLException {
        var preparedStatement = connection.prepareStatement(query);
        for (int i= 0; i < params.length; i++){
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
