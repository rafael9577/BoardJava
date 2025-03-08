package dio.avanade2025.board.persistence.dao;

import dio.avanade2025.board.dto.BoardColumnDTO;
import dio.avanade2025.board.persistence.entity.BoardColumnEntity;

import dio.avanade2025.board.persistence.entity.CardsEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dio.avanade2025.board.persistence.entity.BoardColumnKindEnum.findByName;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?,?,?,?);";
        try (var statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }

            return entity;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<BoardColumnEntity> findByForId(Long boardid) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardid);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
            }
        }
        return entities;
    }

    public List<BoardColumnDTO> findByBoardIdDetails(Long boardid) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
                SELECT
                    bc.id,
                    bc.name,
                    bc.kind
                    COUNT(
                        SELECT c.id
                        FROM CARDS c
                        WHERE c.board_column_id = bc.id
                    )cards_amount
                FROM BOARDS_COLUMNS bc
                WHERE board_id = ?
                ORDER BY `order`
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardid);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public Optional<BoardColumnEntity> findById(Long boardid) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = """
                SELECT bc.name,
                       bc.kind,
                       c.id,
                       c.title,
                       c.description
                FROM BOARDS_COLUMNS bc
                INNER JOIN CARDS c
                    ON c.board_column_id = bc.id
                WHERE bc.id = ?
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardid);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("name"));
                entity.setKind(findByName(resultSet.getString("kind")));

                do {
                    var card = new CardsEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
            }
        }
        return Optional.empty();
    }
}
