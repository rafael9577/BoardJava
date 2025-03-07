package dio.avanade2025.board.ui;

import dio.avanade2025.board.persistence.entity.BoardColumnEntity;
import dio.avanade2025.board.persistence.entity.BoardColumnKindEnum;
import dio.avanade2025.board.persistence.entity.BoardEntity;
import dio.avanade2025.board.services.BoardQueryServices;
import dio.avanade2025.board.services.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static dio.avanade2025.board.persistence.config.ConnectionConfig.getConnection;
import static dio.avanade2025.board.persistence.entity.BoardColumnKindEnum.INITIAL;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo escolha uma opção!");
        var option = -1;
        while (true) {
            System.out.println("1- criar board");
            System.out.println("2- selecionar board ");
            System.out.println("3- excluir board");
            System.out.println("4- sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("valor invalido.");
            }
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("informe o id do board excluido");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("o Board de ID %s foi excluido!\n", id);
            } else {
                System.out.printf("O Board de ID %s não foi excluido!\n", id);
            }
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var queryService = new BoardQueryServices(connection);
            var optional = queryService.findById(id);

            if (optional.isPresent()) {
                var boardMenu = new BoardMenu(optional.get());
                boardMenu.execute();
            } else {
                System.out.printf("não foi encontrado um board com id %s\n", id);
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("informe o nome do novo Board");
        entity.setName(scanner.next());

        System.out.println("seu board terá colunas além das 3 padrões, se sim informe quantas, se não digite 0");
        var additionalcollumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("informe o nome da coluna inicial do board");
        var initialColumnsName = scanner.next();

        var initialColumn = createColumn(initialColumnsName, INITIAL, 0);
        columns.add(initialColumn);
        for (int i = 0; i < additionalcollumns; i++) {
            System.out.println("informe o nome da coluna de tarefa pensente");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, INITIAL, i + 1);
            columns.add(pendingColumn);
        }
        for (int i = 0; i < additionalcollumns; i++) {
            System.out.println("informe o nome da coluna final");
            var finalColumnName = scanner.next();
            var finalColumn = createColumn(finalColumnName, INITIAL, i + 1);
            columns.add(finalColumn);
        }
        for (int i = 0; i < additionalcollumns; i++) {
            System.out.println("informe o nome da coluna de cancelamento");
            var cancelColumnName = scanner.next();
            var cancelColumn = createColumn(cancelColumnName, INITIAL, i + 1);
            columns.add(cancelColumn);

            entity.setBoardColumns(columns);
            try (var connection = getConnection()) {
                var service = new BoardService(connection);
                service.insert(entity);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }


}
