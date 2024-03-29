package model.score;

public enum PieceScore {
    BISHOP(3),
    KING(0),
    KNIGHT(2.5),
    PAWN(1),
    PAWN_DEDUCTION(0.5),
    QUEEN(9),
    ROOK(5),
    SQUARE(0);

    private final double value;

    PieceScore(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
