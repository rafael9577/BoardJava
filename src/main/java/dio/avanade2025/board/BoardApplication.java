package dio.avanade2025.board;

import dio.avanade2025.board.persistence.migration.MigrationStrategy;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

import static dio.avanade2025.board.persistence.config.ConnectionConfig.getConnection;


@SpringBootApplication
public class BoardApplication {

    public static void main(String[] args) throws SQLException {
        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
    }

}
