package view.dto;

import java.util.List;

public record PieceInfo(
        int file,
        int rank,
        char role
) {

    public static List<PieceInfo> from(final List<ChessPiece> chessPieces) {
        return chessPieces.stream()
                          .map(chessPiece -> new PieceInfo(chessPiece.file().index(), chessPiece.rank().index(), chessPiece.symbol().charAt(0)))
                          .toList();
    }
}
