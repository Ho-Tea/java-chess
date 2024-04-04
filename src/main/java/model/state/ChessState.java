package model.state;

import model.chessboard.GameResult;
import model.piece.Color;
import model.piece.Piece;
import model.position.Position;

import java.util.Map;

public class ChessState {
    private FactionState factionState;

    private ChessState(final FactionState factionState) {
        this.factionState = factionState;
    }

    public ChessState() {
        this(new WhiteFaction());
    }

    public static ChessState infuse(final String color) {
        if (color.equals(Color.BLACK.name())) {
            return new ChessState(new BlackFaction());
        }
        return new ChessState();
    }

    public void checkTheTurn(final Piece piece) {
        factionState.checkSameFaction(piece);
    }

    public void passTheTurn() {
        this.factionState = factionState.pass();
    }

    public void validateCheck(final Map<Position, Piece> chessBoard) {
        if (factionState.isCheck(chessBoard)) {
            factionState = factionState.check();
        }
    }

    public void validateTriggerOfCheck(final Map<Position, Piece> chessBoard) {
        if (factionState.isCheck(chessBoard)) {
            throw new IllegalArgumentException("해당 방향으로의 이동은 Check를 유발합니다.");
        }
    }

    public boolean checkMate(final Map<Position, Piece> chessBoard) {
        return factionState.defeat(chessBoard);
    }

    public Color nextTurn() {
        return factionState.oppositeFaction();
    }

    public GameResult winner() {
        if (factionState.oppositeFaction() == Color.BLACK) {
            return GameResult.BLACK;
        }
        return GameResult.WHITE;
    }
}
