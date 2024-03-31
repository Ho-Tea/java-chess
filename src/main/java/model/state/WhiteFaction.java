package model.state;

import model.piece.Color;
import model.piece.Piece;
import model.position.Position;

import java.util.Map;

public sealed class WhiteFaction implements FactionState permits WhiteFactionCheck {
    @Override
    public void checkSameFaction(final Piece piece) {
        if (piece.color() != Color.WHITE) {
            throw new IllegalArgumentException("하얀색 진영의 기물을 이동해야 합니다.");
        }
    }

    @Override
    public FactionState pass() {
        return new BlackFaction();
    }

    @Override
    public boolean isCheck(final Map<Position, Piece> chessBoard) {
        if (checkMate(chessBoard, Color.WHITE)) {
            return false;
        }
        Position kingPosition = positionOfKing(chessBoard, Color.WHITE);
        Map<Position, Piece> enemyFaction = factionOf(chessBoard, Color.BLACK);
        return enemyFaction.entrySet()
                           .stream()
                           .anyMatch(entry -> possibleAttacked(chessBoard, kingPosition, entry));
    }

    @Override
    public FactionState check() {
        return new WhiteFactionCheck();
    }

    @Override
    public boolean defeat(final Map<Position, Piece> chessBoard) {
        return checkMate(chessBoard, Color.WHITE);
    }

    @Override
    public Color oppositeFaction() {
        return Color.BLACK;
    }
}
