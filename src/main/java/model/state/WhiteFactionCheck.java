package model.state;

import model.piece.Color;
import model.piece.Piece;
import model.position.Position;

import java.util.Map;

public final class WhiteFactionCheck extends WhiteFaction {
    @Override
    public boolean isCheck(final Map<Position, Piece> chessBoard) {
        if (super.possibleCheckMate(chessBoard, Color.WHITE, Color.BLACK)) {
            return false;
        }
        if (super.isCheck(chessBoard)) {
            throw new IllegalArgumentException("해당 방향으로의 이동은 Check를 해소할 수 없습니다.");
        }
        return false;
    }

    @Override
    public FactionState check() {
        return this;
    }

    @Override
    public boolean defeat(Map<Position, Piece> chessBoard) {
        return checkMate(chessBoard, Color.WHITE);
    }
}
