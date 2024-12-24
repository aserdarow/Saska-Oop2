package serdarov05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void testInitialSetup() {
        Board board = new Board();
        Piece[][] state = board.getBoard();

        // Проверяем, что начальная доска содержит правильные фигуры
        assertEquals(Piece.BLACK, state[0][1], "Черная фигура должна быть на (0,1)");
        assertEquals(Piece.BLACK, state[2][7], "Черная фигура должна быть на (2,7)");

        assertEquals(Piece.WHITE, state[5][0], "Белая фигура должна быть на (5,0)");
        assertEquals(Piece.WHITE, state[7][6], "Белая фигура должна быть на (7,6)");

        // Проверяем пустые клетки
        assertEquals(Piece.EMPTY, state[3][3], "Позиция (3,3) должна быть пустой");
        assertEquals(Piece.EMPTY, state[4][4], "Позиция (4,4) должна быть пустой");
    }

    @Test
    void testValidMove() {
        Board board = new Board();

        // Ход белых вперед
        assertTrue(board.isValidMove(5, 0, 4, 1, Piece.WHITE), "Ход вперед должен быть допустимым");
        board.movePiece(5, 0, 4, 1);
        assertEquals(Piece.WHITE, board.getBoard()[4][1], "Фигура должна переместиться на (4,1)");
    }

    @Test
    void testInvalidMove() {
        Board board = new Board();

        // Ход белых назад (недопустимо для обычных фигур)
        assertFalse(board.isValidMove(5, 0, 6, 1, Piece.WHITE), "Ход назад для обычной фигуры недопустим");
    }

    @Test
    void testCaptureSinglePiece() {
        Board board = new Board();

        // Подготовка доски для захвата
        board.movePiece(5, 0, 4, 1); // Белые двигаются
        board.movePiece(2, 1, 3, 2); // Черные двигаются

        // Проверка захвата
        assertFalse(board.isValidMove(4, 1, 2, 3, Piece.WHITE), "Захват черной фигуры должен быть допустимым");
        board.movePiece(4, 1, 2, 3);

        // Проверяем результаты
        assertEquals(Piece.WHITE, board.getBoard()[2][3], "Белая фигура должна быть на (2,3)");
        assertEquals(Piece.EMPTY, board.getBoard()[3][2], "Захваченная черная фигура должна исчезнуть");
    }

    @Test
    void testKingPromotion() {
        Board board = new Board();

        // Белая фигура доходит до последнего ряда
        board.movePiece(5, 0, 4, 1);
        board.movePiece(2, 1, 3, 2);
        board.movePiece(4, 1, 3, 0);
        board.movePiece(1, 2, 2, 3);
        board.movePiece(3, 0, 1, 2); // Двигаем белую фигуру на предпоследний ряд
        board.movePiece(0, 3, 1, 4);

        board.movePiece(1, 2, 0, 3); // Белая фигура достигает последнего ряда

        assertEquals(Piece.WHITE_KING, board.getBoard()[0][3], "Фигура должна стать королем на (0,3)");
    }

    @Test
    void testNoMovesLeft() {
        Board board = new Board();

        // Удаляем все черные фигуры, чтобы у черных не осталось ходов
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col += 2) {
                board.getBoard()[row][col + (row % 2)] = Piece.EMPTY;
            }
        }

        assertFalse(board.hasNoMoves(Piece.BLACK), "Черные не должны иметь допустимых ходов");
    }

    @Test
    void testMultipleCaptures() {
        Board board = new Board();

        // Подготовка доски для множественного захвата
        board.movePiece(5, 0, 4, 1);
        board.movePiece(2, 1, 3, 2);
        board.movePiece(4, 1, 2, 3); // Белая фигура захватывает черную
        board.movePiece(2, 3, 0, 5); // Белая фигура выполняет второй захват

        assertEquals(Piece.WHITE_KING, board.getBoard()[0][5], "Белая фигура должна быть на (0,5)");
    }

    @Test
    void testGameEndCondition() {
        Board board = new Board();

        // Убираем все черные фигуры для завершения игры
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                board.getBoard()[row][col] = Piece.EMPTY;
            }
        }

        assertTrue(board.hasNoMoves(Piece.BLACK), "У черных не осталось ходов");
        assertEquals("White", board.checkWinner(), "Победителем должны быть белые");
    }
}
