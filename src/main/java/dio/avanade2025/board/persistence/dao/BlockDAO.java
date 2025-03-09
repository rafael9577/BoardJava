package dio.avanade2025.board.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static dio.avanade2025.board.persistence.converter.OffsetDateTimeConverter.toTimesTamp;


@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException {
        var sql = """
                INSERT INTO BLOCKS (blocked_at, block_reason, cards_id) VALUES (?, ?, ?);
                """;
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimesTamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

}
