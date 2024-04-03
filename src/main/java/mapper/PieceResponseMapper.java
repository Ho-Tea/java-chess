package mapper;

import model.chessboard.ChessBoard;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.RoleStatus;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import view.ChessSymbol;
import view.dto.PieceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PieceResponseMapper {

    private PieceResponseMapper() {
        throw new AssertionError("정적 유틸 클래스는 생성자를 호출할 수 없습니다.");
    }

    public static List<PieceResponse> toPieceInfo(final ChessBoard chessBoard) {
        List<PieceResponse> pieceResponses = new ArrayList<>();
        Map<Position, Piece> pieces = chessBoard.getChessBoard();
        for (Rank rank : Rank.values()) {
            fillRow(pieces, rank, pieceResponses);
        }
        return pieceResponses;
    }

    private static void fillRow(final Map<Position, Piece> pieces, final Rank rank, final List<PieceResponse> pieceResponses) {
        for (File file : File.values()) {
            Piece piece = pieces.get(Position.of(file, rank));
            PieceResponse pieceResponse = new PieceResponse(file.index(), rank.index(), abbreviation(piece));
            pieceResponses.add(pieceResponse);
        }
    }

    private static char abbreviation(final Piece piece) {
        RoleStatus role = piece.roleStatus();
        ChessSymbol chessSymbol = chessSymbol(role);
        if (piece.color() == Color.BLACK) {
            return chessSymbol.getBlackFactionAbbreviation();
        }
        return chessSymbol.getWhiteFactionAbbreviation();
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
