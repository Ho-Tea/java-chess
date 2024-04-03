package mapper;

import entity.PieceEntity;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.*;
import model.position.File;
import model.position.Position;
import model.position.Rank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PieceEntityMapper {
    private static final Map<RoleStatus, Piece> WHITE_PIECES = Map.of(
            RoleStatus.KING, new Piece(new King(Color.WHITE)),
            RoleStatus.KNIGHT, new Piece(new Knight(Color.WHITE)),
            RoleStatus.BISHOP, new Piece(new Bishop(Color.WHITE)),
            RoleStatus.PAWN, new Piece(new Pawn(Color.WHITE)),
            RoleStatus.QUEEN, new Piece(new Queen(Color.WHITE)),
            RoleStatus.ROOK, new Piece(new Rook(Color.WHITE)),
            RoleStatus.SQUARE, new Piece(new Square()));
    private static final Map<RoleStatus, Piece> BLACK_PIECES = Map.of(
            RoleStatus.KING, new Piece(new King(Color.BLACK)),
            RoleStatus.KNIGHT, new Piece(new Knight(Color.BLACK)),
            RoleStatus.BISHOP, new Piece(new Bishop(Color.BLACK)),
            RoleStatus.PAWN, new Piece(new Pawn(Color.BLACK)),
            RoleStatus.QUEEN, new Piece(new Queen(Color.BLACK)),
            RoleStatus.ROOK, new Piece(new Rook(Color.BLACK)),
            RoleStatus.SQUARE, new Piece(new Square()));


    private PieceEntityMapper() {
        throw new AssertionError("정적 유틸 클래스는 생성자를 호출할 수 없습니다.");
    }

    public static Map<Position, Piece> toChessBoard(final List<PieceEntity> pieceEntities) {
        Map<Position, Piece> chess = new LinkedHashMap<>();
        pieceEntities.forEach(pieceEntity -> {
                    RoleStatus roleStatus = RoleStatus.from(pieceEntity.role());
                    Piece piece = WHITE_PIECES.get(roleStatus);
                    if (pieceEntity.color() == Color.BLACK.name()) {
                        piece = BLACK_PIECES.get(roleStatus);
                    }
                    chess.put(Position.of(File.fromIndex(pieceEntity.file()), Rank.fromIndex(pieceEntity.rank())), piece);
                }
        );
        return chess;
    }

    public static List<PieceEntity> toPieceEntities(final Map<Position, Piece> chessBoard) {
        List<PieceEntity> pieceEntities = new ArrayList<>();
        for (Position position : chessBoard.keySet()) {
            Piece piece = chessBoard.get(position);
            pieceEntities.add(new PieceEntity(
                    null,
                    position.rank().index(),
                    position.file().index(),
                    piece.color().name(),
                    piece.roleStatus().name())
            );
        }
        return pieceEntities;
    }

    public static PieceEntity toSquarePieceEntity(final PieceEntity pieceEntity) {
        return pieceEntity.changedSymbol(Color.UN_COLORED.name(), RoleStatus.SQUARE.name());
    }
}
