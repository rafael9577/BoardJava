package dio.avanade2025.board.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import dio.avanade2025.board.dto.CardDetailsDTO;
import dio.avanade2025.board.persistence.entity.CardsEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

import static dio.avanade2025.board.persistence.converter.OffsetDateTimeConverter.toOffsetDAteTime;
import static dio.avanade2025.board.persistence.converter.OffsetDateTimeConverter.toTimesTamp;
import static java.util.Objects.nonNull;


@AllArgsConstructor
public class CardDAO {
    private Connection connection;

    public CardsEntity insert(final CardsEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id, create_at) VALUES (?,?,?,?)";
        try (var statement = connection.prepareStatement(sql) ){
            var i = 1;
            statement.setString(i ++, entity.getTitle());
            statement.setString(i ++, entity.getDescription());
            statement.setLong(i++, entity.getBoardColumn().getId());
            statement.setTimestamp(i , toTimesTamp(OffsetDateTime.now()));
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException{
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++, columnId);
            statement.setLong(i , cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) {
        var sql = """
                    SELECT c.id,
                           c.title,
                           c.description,
                           b.blocked_at,
                           b.block_reason,
                           c.board_column_id,
                           bc.name,
                           (SELECT COUNT(sub_b.id)
                                    FROM BLOCKS sub_b
                                    WHERE sub_b.cards_id = c.id)block_amount
                      FROM CARDS c
                      LEFT JOIN BLOCKS b
                        ON c.id = b.cards_id
                        AND b.unblocked_at IS NULL
                      INNER JOIN BOARDS_COLUMNS bc
                        ON bc.id = c.board_column_id
                      WHERE c.id = ?;
                """;
        try (var stantement = connection.prepareStatement(sql)) {
            stantement.setLong(1, id);
            stantement.executeQuery();
            var resultSet = stantement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDAteTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")

                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
