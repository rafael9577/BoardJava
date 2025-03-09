package dio.avanade2025.board.dto;

import dio.avanade2025.board.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {

}
