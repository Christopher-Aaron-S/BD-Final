package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static final String URL = "jdbc:postgresql://localhost:5432/ProjectBD";
    private static final String USER = "postgres";
    private static final String PASSWORD = "CaaronS050106!p";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
