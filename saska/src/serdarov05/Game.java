package serdarov05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JPanel {
    private static final int CELL_SIZE = 75; // Размер клетки
    private final Board board = new Board();
    private int selectedRow = -1, selectedCol = -1;
    private boolean whiteTurn = true;

    public Game() {
        setPreferredSize(new Dimension(600, 600));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;

                // Обработка выбора шашки или хода
                if (selectedRow == -1) {
                    if (isPlayerPiece(row, col)) {
                        selectedRow = row;
                        selectedCol = col;
                        repaint();
                    }
                } else {
                    if (board.isValidMove(selectedRow, selectedCol, row, col, whiteTurn ? Piece.WHITE : Piece.BLACK)) {
                        board.movePiece(selectedRow, selectedCol, row, col);
                        whiteTurn = !whiteTurn; // Смена хода
                        selectedRow = -1;
                        selectedCol = -1;
                        repaint();
                        checkWinner();  // Проверяем победителя после каждого хода
                    } else {
                        // Если ход невалидный, снимаем выделение
                        selectedRow = -1;
                        selectedCol = -1;
                        repaint();
                    }
                }
            }
        });
    }

    private void checkWinner() {
        // Определяем, у кого сейчас ход
        Piece opponent = whiteTurn ? Piece.BLACK : Piece.WHITE;

        // Проверяем, остались ли ходы у противника
        if (board.hasNoMoves(opponent)) {
            String winner = whiteTurn ? "Белые" : "Черные";
            JOptionPane.showMessageDialog(this, "Игра окончена! Победили " + winner + "!",
                    "Победа", JOptionPane.INFORMATION_MESSAGE);
            disableBoard(); // Делаем доску неактивной
        }
    }

    // Метод для отключения доски после завершения игры
    private void disableBoard() {
        // Убираем MouseListener для блокировки дальнейших ходов
        this.removeMouseListener(this.getMouseListeners()[0]);
        repaint();
    }

    // Проверка, можно ли выбрать данную шашку
    private boolean isPlayerPiece(int row, int col) {
        Piece piece = board.getBoard()[row][col];
        if (whiteTurn) {
            return piece == Piece.WHITE || piece == Piece.WHITE_KING;
        } else {
            return piece == Piece.BLACK || piece == Piece.BLACK_KING;
        }
    }

    // Отрисовка доски и шашек
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Рисование клетки
                if ((row + col) % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Подсветка выбранной шашки
                if (row == selectedRow && col == selectedCol) {
                    g.setColor(new Color(255, 255, 0, 128)); // Полупрозрачный жёлтый
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                // Рисование шашек
                Piece piece = board.getBoard()[row][col];
                if (piece == Piece.WHITE) {
                    drawPiece(g, col, row, Color.WHITE);
                } else if (piece == Piece.BLACK) {
                    drawPiece(g, col, row, Color.BLACK);
                } else if (piece == Piece.WHITE_KING) {
                    drawPiece(g, col, row, Color.WHITE);
                    drawKing(g, col, row, Color.RED);
                } else if (piece == Piece.BLACK_KING) {
                    drawPiece(g, col, row, Color.BLACK);
                    drawKing(g, col, row, Color.RED);
                }
            }
        }
    }

    // Метод для рисования шашки
    private void drawPiece(Graphics g, int col, int row, Color color) {
        g.setColor(color);
        g.fillOval(col * CELL_SIZE + 10, row * CELL_SIZE + 10, CELL_SIZE - 20, CELL_SIZE - 20);
    }

    // Рисование короны для дамки
    private void drawKing(Graphics g, int col, int row, Color crownColor) {
        g.setColor(crownColor);
        String kingSymbol = "K";
        FontMetrics metrics = g.getFontMetrics();
        int x = col * CELL_SIZE + (CELL_SIZE - metrics.stringWidth(kingSymbol)) / 2;  // Центр по горизонтали
        int y = row * CELL_SIZE + (CELL_SIZE - metrics.getHeight()) / 2 + metrics.getAscent();  // Центр по вертикали
        g.drawString(kingSymbol, x, y);
    }
}
