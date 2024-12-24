package serdarov05;

public enum Piece {
    EMPTY,
    BLACK,
    WHITE,
    BLACK_KING,
    WHITE_KING;

    public Piece toKing() {
        if (this == BLACK) {
            return BLACK_KING;
        } else if (this == WHITE) {
            return WHITE_KING;
        }
        return this; // Если уже король или EMPTY
    }
}
