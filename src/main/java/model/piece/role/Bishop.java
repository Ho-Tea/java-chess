package model.piece.role;

import model.direction.ShiftPattern;
import model.piece.Color;
import model.score.PieceScore;
import model.shift.MultiShift;

public final class Bishop extends Role {
    public Bishop(final Color color) {
        super(color, new MultiShift(ShiftPattern.BISHOP));
    }

    @Override
    public RoleStatus status() {
        return RoleStatus.BISHOP;
    }

    @Override
    public PieceScore score() {
        return PieceScore.BISHOP;
    }
}
