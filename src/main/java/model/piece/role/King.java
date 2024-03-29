package model.piece.role;

import model.direction.ShiftPattern;
import model.piece.Color;
import model.score.PieceScore;
import model.shift.SingleShift;

public final class King extends Role {
    public King(final Color color) {
        super(color, new SingleShift(ShiftPattern.KING));
    }

    @Override
    public RoleStatus status() {
        return RoleStatus.KING;
    }

    @Override
    public PieceScore score() {
        return PieceScore.KING;
    }
}
