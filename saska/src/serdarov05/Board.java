package serdarov05;

public class Board {
    private static final int SIZE = 8; // Размер доски
    private Piece[][] board;

    private int selectedRow = -1; // Координаты выбранной шашки
    private int selectedCol = -1;
    private boolean pieceSelected = false; // Флаг выделения шашки

    public Board() {
        board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    /**
     * Проверка победителя на основе отсутствия ходов у одного из игроков.
     */
    public String checkWinner() {
        if (hasNoMoves(Piece.BLACK)) {
            return "White"; // Черные не могут ходить - победа белых
        } else if (hasNoMoves(Piece.WHITE)) {
            return "Black"; // Белые не могут ходить - победа черных
        }
        return "None"; // Игра продолжается
    }

    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Инициализация доски шашками в начальном расположении.
     */
    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) board[row][col] = Piece.BLACK;
                    else if (row > 4) board[row][col] = Piece.WHITE;
                    else board[row][col] = Piece.EMPTY;
                } else {
                    board[row][col] = Piece.EMPTY;
                }
            }
        }
    }

    /**
     * Проверка, можно ли выбрать шашку текущего игрока.
     */
    public boolean selectPiece(int row, int col, Piece player) {
        if (!isInBounds(row, col)) return false; // Проверка на границы доски
        Piece piece = board[row][col];

        if (piece == player || piece == getKing(player)) { // Если шашка принадлежит игроку
            selectedRow = row;
            selectedCol = col;
            pieceSelected = true;
            return true;
        }

        pieceSelected = false;
        return false; // Шашка не принадлежит игроку
    }

    public boolean isPieceSelected() {
        return pieceSelected;
    }

    /**
     * Проверка на отсутствие ходов у всех шашек игрока.
     */
    public boolean hasNoMoves(Piece player) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == player || board[row][col] == getKing(player)) {
                    if (hasValidMove(row, col, player)) {
                        return false; // Есть хотя бы один допустимый ход
                    }
                }
            }
        }
        return true; // Ходов нет
    }

    /**
     * Проверка наличия допустимых ходов для данной шашки.
     */
    private boolean hasValidMove(int x, int y, Piece player) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; // Возможные направления хода
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValidMove(x, y, newX, newY, player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка допустимости хода для обычной шашки или дамки.
     */
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Piece player) {
        if (!isInBounds(toRow, toCol) || board[toRow][toCol] != Piece.EMPTY) return false; // Целевая клетка занята

        Piece piece = board[fromRow][fromCol];
        if (isKing(piece)) {
            return isValidKingMove(fromRow, fromCol, toRow, toCol, player);
        }

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        // Обычный ход вперед
        if (player == Piece.WHITE && rowDiff == -1 && colDiff == 1) return true;
        if (player == Piece.BLACK && rowDiff == 1 && colDiff == 1) return true;

        // Проверка на захват шашки
        if (Math.abs(rowDiff) == 2 && colDiff == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            return isOpponent(piece, board[midRow][midCol]);
        }

        return false;
    }

    /**
     * Логика хода для дамки.
     */
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol, Piece player) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        if (rowDiff != colDiff) return false; // Ход не по диагонали

        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;
        boolean captured = false;

        while (currentRow != toRow) {
            if (board[currentRow][currentCol] != Piece.EMPTY) {
                if (!isOpponent(player, board[currentRow][currentCol]) || captured) return false; // Захват уже был
                captured = true;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }

    /**
     * Перемещение шашки с обработкой захвата и превращением в дамку.
     */
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board[fromRow][fromCol];
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = Piece.EMPTY;

        // Обработка захвата
        if (Math.abs(toRow - fromRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            board[midRow][midCol] = Piece.EMPTY;
        }

        // Превращение в дамку
        if (piece == Piece.WHITE && toRow == 0) board[toRow][toCol] = Piece.WHITE_KING;
        if (piece == Piece.BLACK && toRow == 7) board[toRow][toCol] = Piece.BLACK_KING;
    }

    /**
     * Проверка на противника.
     */
    private boolean isOpponent(Piece player, Piece other) {
        return other != Piece.EMPTY && other != player && other != getKing(player);
    }

    private Piece getKing(Piece piece) {
        return piece == Piece.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
    }

    private boolean isKing(Piece piece) {
        return piece == Piece.WHITE_KING || piece == Piece.BLACK_KING;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
