package dio.avanade2025.board.persistence.config;

import static lombok.AccessLevel.PRIVATE;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost/board?createDatabaseIfNotExist=true";
        var user = "root";
        var password = "123!@#ABCdef";

        var connection = DriverManager.getConnection( url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

}
