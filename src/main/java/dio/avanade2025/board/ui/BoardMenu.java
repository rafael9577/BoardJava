package dio.avanade2025.board.ui;

import dio.avanade2025.board.persistence.entity.BoardColumnEntity;
import dio.avanade2025.board.persistence.entity.BoardEntity;
import dio.avanade2025.board.services.BoarColumnQueryService;
import dio.avanade2025.board.services.BoardQueryServices;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static dio.avanade2025.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;


    public void execute() throws SQLException {
        System.out.printf("Bem vindo ao Board %s de Id %d, escolha uma das opções!", entity.getName(), entity.getId());
        var option = -1;
        while (true) {
            System.out.println("1- criar card");
            System.out.println("2- Mover um card");
            System.out.println("3- Bloquear um card");
            System.out.println("4- desbloquear um card");
            System.out.println("5- cancelar um card");
            System.out.println("6- Voltar para o menu anterior");

            System.out.println("7- Visualizar colunas");
            System.out.println("8- Visualizar coluna com cards");
            System.out.println("9- Visualizar cards");
            System.out.println("0- sair");

            option = scanner.nextInt();
            switch (option) {
                case 1 -> createcard();
                case 2 -> movercard();
                case 3 -> bloquearcard();
                case 4 -> desbloquear();
                case 5 -> cancelar();
                case 6 -> voltar();

                case 7 -> showBoard();
                case 8 -> showColumns();
                case 9 -> showCards();
                case 0 -> System.exit(0);

                default -> System.out.println("valor invalido.");
            }
        }
    }

    private void showCards() {
    }

    private void showColumns() throws SQLException {
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectrdColumn = -1L;
        while (!columnIds.contains(selectrdColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(
                    c -> System.out.printf("%s - $s\n", c.getId(), c.getName())
            );
            selectrdColumn = scanner.nextLong();
        }
        try (var connection = getConnection()){
            var column = new BoarColumnQueryService(connection).findById(selectrdColumn);
            column.ifPresent( co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca-> {
                    System.out.printf("card %s - %s.\n Descrição: $s",
                            ca.getId(), ca.getTitle(), ca.getDescription()
                    );
                });
            });

        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryServices(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("board [%s, %s]\n", b.id(), b.name());
                b.column().forEach(c -> {
                    System.out.printf("coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        }
    }

    private void voltar() {
        System.out.println("voltando para o menu anterior");
    }

    private void cancelar() {
    }

    private void desbloquear() {
    }

    private void bloquearcard() {
    }

    private void movercard() {
    }

    private void createcard() {
    }


}
