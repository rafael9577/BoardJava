package dio.avanade2025.board.dto;


import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                          String title,
                          String description,
                          boolean blocked,
                          OffsetDateTime blockAt,
                          String blockReason,
                          int blockAmount,
                          Long columnId,
                          String ColumnName) {
}
