package dio.avanade2025.board.persistence.entity;

import lombok.Data;

@Data
public class CardsEntity {

    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();

}
