package dio.avanade2025.board.ui;

import dio.avanade2025.board.dto.BoardColumnInfoDTO;
import dio.avanade2025.board.persistence.entity.BoardColumnEntity;
import dio.avanade2025.board.persistence.entity.BoardEntity;
import dio.avanade2025.board.persistence.entity.CardsEntity;
import dio.avanade2025.board.services.BoarColumnQueryService;
import dio.avanade2025.board.services.BoardQueryServices;
import dio.avanade2025.board.services.CardQueryService;
import dio.avanade2025.board.services.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static dio.avanade2025.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;


    public void execute() throws SQLException {
        System.out.printf("Bem vindo ao Board %s de Id %d, escolha uma das opções!\n", entity.getName(), entity.getId());
        var option = -1;
        while (option != 6) {
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
                case 1 -> createcard(); // completo
                case 2 -> movercard(); //completo
                case 3 -> bloquearcard(); // complete
                case 4 -> desbloquear(); //
                case 5 -> cancelar(); //completo
                case 6 -> System.out.println("voltando para o menu anterior"); //completo

                case 7 -> showBoard(); //completo
                case 8 -> showColumns(); //completo
                case 9 -> showCards(); //completo
                case 0 -> System.exit(0); //completo

                default -> System.out.println("valor invalido.");
            }
        }
    }

    private void showCards() throws SQLException {
        System.out.println("informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId).ifPresentOrElse(c -> {
                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                System.out.printf("Descrição: %s\n", c.description());
                System.out.printf(c.blocked() ? "Está bloquado por motivo %s\n" : "Não está bloquado\n", c.blockReason());
                System.out.printf("Já foi bloqueado %s vezes\n", c.blockAmount());
                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.ColumnName());
            }, () -> System.out.printf("não existe um card com id %s\n", selectedCardId));
        }
    }

    private void showColumns() throws SQLException {
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectrdColumn = -1L;
        while (!columnIds.contains(selectrdColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectrdColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoarColumnQueryService(connection).findById(selectrdColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> {
                    System.out.printf("card %s - %s.\n Descrição: %s\n", ca.getId(), ca.getTitle(), ca.getDescription());
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

    private void cancelar() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void desbloquear() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        try (var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void bloquearcard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo);

        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void movercard() throws SQLException {
        System.out.println("informe o id do card que deseja mover para a proxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createcard() throws SQLException {
        var card = new CardsEntity();
        System.out.println("informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }
    }


}
