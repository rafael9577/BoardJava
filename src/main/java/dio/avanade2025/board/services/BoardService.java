package dio.avanade2025.board.services;

import dio.avanade2025.board.persistence.dao.BoardColumnDAO;
import dio.avanade2025.board.persistence.dao.BoardDAO;
import dio.avanade2025.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try{
            if (!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try {
            dao.insert(entity);
            var columns = new ArrayList<>(entity.getBoardColumns());
            columns.forEach(c -> c.setBoard(entity));
            for (var column: columns) {
                boardColumnDAO.insert(column);
            }
            connection.commit();
            System.out.println('a');
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        return entity;
    }
}
