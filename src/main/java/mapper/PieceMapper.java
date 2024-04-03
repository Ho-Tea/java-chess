package mapper;

import entity.PieceEntity;
import model.chessboard.ChessBoard;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.*;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import model.state.ChessState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PieceMapper {
    public static ChessBoard toChessBoard(final List<PieceEntity> pieceEntities) {
        Map<Position, Piece> chess = new LinkedHashMap<>();
        pieceEntities.forEach(pieceEntry -> chess.put(
                Position.of(File.fromIndex(pieceEntry.file()), Rank.fromIndex(pieceEntry.rank())),
                fromChessSymbol(pieceEntry.symbol())));
        return new ChessBoard(chess, new ChessState());
    }

    private static Piece fromChessSymbol(final String symbol) {
        return switch (symbol.charAt(0)) {
            case 'k' -> new Piece(new King(Color.WHITE));
            case 'q' -> new Piece(new Queen(Color.WHITE));
            case 'p' -> new Piece(new Pawn(Color.WHITE));
            case 'b' -> new Piece(new Bishop(Color.WHITE));
            case 'n' -> new Piece(new Knight(Color.WHITE));
            case 'r' -> new Piece(new Rook(Color.WHITE));
            case 'K' -> new Piece(new King(Color.BLACK));
            case 'Q' -> new Piece(new Queen(Color.BLACK));
            case 'P' -> new Piece(new Pawn(Color.BLACK));
            case 'B' -> new Piece(new Bishop(Color.BLACK));
            case 'N' -> new Piece(new Knight(Color.BLACK));
            case 'R' -> new Piece(new Rook(Color.BLACK));
            case '.' -> new Piece(new Square());
            default -> throw new IllegalStateException("특정 Symbol에 적합한 Piece가 존재하지 않습니다.");
        };
    }
}
