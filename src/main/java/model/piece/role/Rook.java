package model.piece.role;


import model.direction.ShiftPattern;
import model.piece.Color;
import model.score.PieceScore;
import model.shift.MultiShift;

public final class Rook extends Role {
    public Rook(final Color color) {
        super(color, new MultiShift(ShiftPattern.ROOK));
    }

    @Override
    public RoleStatus status() {
        return RoleStatus.ROOK;
    }

    @Override
    public PieceScore score() {
        return PieceScore.ROOK;
    }
}
