package dio.avanade2025.board.services;

import dio.avanade2025.board.dto.BoardColumnInfoDTO;
import dio.avanade2025.board.exception.CardBlockedException;
import dio.avanade2025.board.exception.CardFinishedExcept;
import dio.avanade2025.board.exception.EntityNotFoundException;
import dio.avanade2025.board.persistence.dao.BlockDAO;
import dio.avanade2025.board.persistence.dao.CardDAO;
import dio.avanade2025.board.persistence.entity.CardsEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static dio.avanade2025.board.persistence.entity.BoardColumnKindEnum.CANCEL;
import static dio.avanade2025.board.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public CardsEntity insert(final CardsEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(dto.id());
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("o Card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedExcept("O card já foi finalizados");
            }
            var mextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow();
            dao.moveToColumn(mextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(dto.id());
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("o Card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedExcept("O card já foi finalizados");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow();
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            if (dto.blocked()) {
                var message = "O card %s já está bloqueado".formatted(dto.id());
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.id().equals(dto.id()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                var message = "O card esta em uma coluna do tipo %s e não pode ser bloqueado"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDao = new BlockDAO(connection);
            blockDao.block(id, reason);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            if (!dto.blocked()) {
                var message = "O card %s já está desbloqueado".formatted(dto.id());
                throw new CardBlockedException(message);
            }
            var blockDao = new BlockDAO(connection);
            blockDao.unblock(id, reason);
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }

}
