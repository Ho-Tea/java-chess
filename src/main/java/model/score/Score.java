package model.score;

import model.piece.Color;
import model.piece.Piece;
import model.piece.role.RoleStatus;
import model.position.File;
import model.position.Position;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record Score(double value) {

    public static Score of(final Map<Position, Piece> chessBoard, final Color color) {
        double score = 0.0;
        Set<Position> positions = chessBoard.keySet();
        for (File file : File.values()) {
            List<Position> fileColumn = positions.stream()
                                                 .filter(position -> position.same(file))
                                                 .toList();
            double fileScore = fileColumn.stream()
                                         .map(chessBoard::get)
                                         .filter(piece -> piece.isOccupied() && piece.color() == color)
                                         .map(piece -> piece.score().getValue())
                                         .reduce(0.0, Double::sum);
            long pawnCount = fileColumn.stream()
                                       .map(chessBoard::get)
                                       .filter(piece -> piece.roleStatus() == RoleStatus.PAWN && piece.color() == color)
                                       .count();
            if (pawnCount > 1) {
                fileScore -= pawnCount * PieceScore.PAWN.getValue();
                fileScore += pawnCount * PieceScore.PAWN_DEDUCTION.getValue();
            }
            score += fileScore;
        }
        return new Score(score);
    }
}
