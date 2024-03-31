package model.chessboard;

import model.direction.Destination;
import model.direction.Route;
import model.direction.WayPoints;
import model.piece.Color;
import model.piece.Piece;
import model.position.Position;
import model.score.Score;
import model.state.ChessState;

import java.util.Map;

public class ChessBoard {
    private final Map<Position, Piece> chessBoard;
    private final ChessState chessState;

    public ChessBoard() {
        this.chessBoard = ChessBoardFactory.create();
        this.chessState = new ChessState();
    }

    public void proceedToTurn(final Position source, final Position target) {
        chessState.validateCheck(chessBoard);
        Piece sourcePiece = chessBoard.get(source);
        Piece targetPiece = chessBoard.get(target);
        chessState.checkTheTurn(sourcePiece);
        Route route = sourcePiece.findRoute(source, target);
        WayPoints wayPoints = WayPoints.of(chessBoard, route, target);
        Destination destination = new Destination(target, targetPiece);
        sourcePiece.validateMoving(wayPoints, destination);
        move(source, sourcePiece, destination);
        chessState.passTheTurn();
    }

    private void move(final Position source, final Piece sourcePiece, final Destination destination) {
        Piece targetPiece = new Piece(destination.target());
        try {
            sourcePiece.moveTo(destination);
            chessState.validateTriggerOfCheck(chessBoard);
        } catch (IllegalArgumentException e) {
            revoke(source, destination.target(), new Destination(destination.position(), targetPiece));
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void revoke(final Position source, final Piece sourcePiece, final Destination destination) {
        chessBoard.put(source, sourcePiece);
        chessBoard.put(destination.position(), destination.target());
    }

    public Score score(Color color) {
        return Score.of(chessBoard, color);
    }

    public boolean checkMate() {
        return chessState.checkMate(chessBoard);
    }

    public Color winner() {
        return chessState.winner();
    }

    public Map<Position, Piece> getChessBoard() {
        return Map.copyOf(chessBoard);
    }
}
