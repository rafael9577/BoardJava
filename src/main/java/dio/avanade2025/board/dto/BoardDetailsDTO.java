package dio.avanade2025.board.dto;

import java.util.List;

public record BoardDetailsDTO(Long id,
                             String name,
                             List<BoardColumnDTO> column) {

}
