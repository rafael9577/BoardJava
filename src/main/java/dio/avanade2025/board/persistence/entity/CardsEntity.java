package dio.avanade2025.board.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CardsEntity {

    private Long id;
    private String title;
    private String description;
    private OffsetDateTime createAt;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();

}
