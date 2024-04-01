package util;

import model.chessboard.ChessBoard;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.*;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import model.state.ChessState;
import view.dto.ChessPiece;
import view.dto.PieceInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PieceInfoMapper {

    public enum ChessSymbol {
        KING('K', 'k'),
        QUEEN('Q', 'q'),
        PAWN('P', 'p'),
        BISHOP('B', 'b'),
        KNIGHT('N', 'n'),
        ROOK('R', 'r'),
        SQUARE('.', '.');

        private final char blackFactionAbbreviation;
        private final char whiteFactionAbbreviation;

        ChessSymbol(final char blackFactionAbbreviation, final char whiteFactionAbbreviation) {
            this.blackFactionAbbreviation = blackFactionAbbreviation;
            this.whiteFactionAbbreviation = whiteFactionAbbreviation;
        }
    }

    private PieceInfoMapper() {
        throw new AssertionError("정적 유틸 클래스는 생성자를 호출할 수 없습니다.");
    }

    public static List<PieceInfo> toPieceInfo(final ChessBoard chessBoard) {
        List<PieceInfo> pieceInfos = new ArrayList<>();
        Map<Position, Piece> pieces = chessBoard.getChessBoard();
        for (Rank rank : Rank.values()) {
            fillRow(pieces, rank, pieceInfos);
        }
        return pieceInfos;
    }

    private static void fillRow(final Map<Position, Piece> pieces, final Rank rank, final List<PieceInfo> pieceInfos) {
        for (File file : File.values()) {
            Piece piece = pieces.get(Position.of(file, rank));
            PieceInfo pieceInfo = new PieceInfo(file.index(), rank.index(), abbreviation(piece));
            pieceInfos.add(pieceInfo);
        }
    }

    public static ChessBoard toChessBoard(final List<ChessPiece> chessPieces) {
        List<PieceInfo> pieceInfos = PieceInfo.from(chessPieces);
        Map<Position, Piece> chess = new LinkedHashMap<>();
        pieceInfos.forEach(pieceInfo -> chess.put(Position.of(File.fromIndex(pieceInfo.file()), Rank.fromIndex(pieceInfo.rank()))
                , fromChessSymbol(pieceInfo.role())));
        return new ChessBoard(chess, new ChessState());
    }

    private static Piece fromChessSymbol(final char role) {
        return switch (role) {
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
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };
    }

    private static char abbreviation(final Piece piece) {
        RoleStatus role = piece.roleStatus();
        ChessSymbol chessSymbol = chessSymbol(role);
        if (piece.color() == Color.BLACK) {
            return chessSymbol.blackFactionAbbreviation;
        }
        return chessSymbol.whiteFactionAbbreviation;
    }

    private static ChessSymbol chessSymbol(final RoleStatus role) {
        return switch (role) {
            case KING -> ChessSymbol.KING;
            case BISHOP -> ChessSymbol.BISHOP;
            case KNIGHT -> ChessSymbol.KNIGHT;
            case PAWN -> ChessSymbol.PAWN;
            case QUEEN -> ChessSymbol.QUEEN;
            case ROOK -> ChessSymbol.ROOK;
            case SQUARE -> ChessSymbol.SQUARE;
        };
    }
}
