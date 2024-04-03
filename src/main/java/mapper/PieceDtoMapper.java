package mapper;

import dto.PieceDto;
import model.chessboard.ChessBoard;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.RoleStatus;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import view.ChessSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PieceDtoMapper {
    private static final Map<RoleStatus, ChessSymbol> SYMBOLS = Map.of(
            RoleStatus.KING, ChessSymbol.KING,
            RoleStatus.BISHOP, ChessSymbol.BISHOP,
            RoleStatus.KNIGHT, ChessSymbol.KNIGHT,
            RoleStatus.PAWN, ChessSymbol.PAWN,
            RoleStatus.QUEEN, ChessSymbol.QUEEN,
            RoleStatus.ROOK, ChessSymbol.ROOK,
            RoleStatus.SQUARE, ChessSymbol.SQUARE);

    private PieceDtoMapper() {
        throw new AssertionError("정적 유틸 클래스는 생성자를 호출할 수 없습니다.");
    }

    public static List<PieceDto> toPieceDto(final ChessBoard chessBoard) {
        List<PieceDto> pieceDto = new ArrayList<>();
        Map<Position, Piece> pieces = chessBoard.getChessBoard();
        for (Rank rank : Rank.values()) {
            fillRow(pieces, rank, pieceDto);
        }
        return pieceDto;
    }

    private static void fillRow(final Map<Position, Piece> pieces, final Rank rank, final List<PieceDto> pieceDtos) {
        for (File file : File.values()) {
            Piece piece = pieces.get(Position.of(file, rank));
            PieceDto pieceDto = new PieceDto(file.index(), rank.index(), abbreviation(piece));
            pieceDtos.add(pieceDto);
        }
    }

    private static char abbreviation(final Piece piece) {
        RoleStatus role = piece.roleStatus();
        ChessSymbol chessSymbol = SYMBOLS.get(role);
        if (piece.color() == Color.BLACK) {
            return chessSymbol.getBlackFactionAbbreviation();
        }
        return chessSymbol.getWhiteFactionAbbreviation();
    }
}
