package model.score;

import model.piece.Color;
import model.piece.Piece;
import model.piece.role.*;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    /*

    .KR.....  8
    P.PB....  7
    .P..Q...  6
    ........  5
    .....nq.  4
    .....p.p  3
    .....pp.  2
    ....rk..  1

     */
    @DisplayName("각 진영의 점수를 산출한다.")
    @Test
    void score(){
        Map<Position, Piece> chessBoard = new HashMap<>();
        for (File file : File.values()) {
            for(Rank rank : Rank.values()){
                chessBoard.put(Position.of(file, rank), new Piece(new Square()));
            }
        }
        chessBoard.put(Position.of(File.A, Rank.SEVEN), new Piece(new Pawn(Color.BLACK)));
        chessBoard.put(Position.of(File.B, Rank.EIGHT), new Piece(new King(Color.BLACK)));
        chessBoard.put(Position.of(File.B, Rank.SIX), new Piece(new Pawn(Color.BLACK)));
        chessBoard.put(Position.of(File.C, Rank.EIGHT), new Piece(new Rook(Color.BLACK)));
        chessBoard.put(Position.of(File.C, Rank.SEVEN), new Piece(new Pawn(Color.BLACK)));
        chessBoard.put(Position.of(File.D, Rank.SEVEN), new Piece(new Bishop(Color.BLACK)));
        chessBoard.put(Position.of(File.E, Rank.SIX), new Piece(new Queen(Color.BLACK)));
        chessBoard.put(Position.of(File.E, Rank.ONE), new Piece(new Rook(Color.WHITE)));
        chessBoard.put(Position.of(File.F, Rank.FOUR), new Piece(new Knight(Color.WHITE)));
        chessBoard.put(Position.of(File.F, Rank.THREE), new Piece(new Pawn(Color.WHITE)));
        chessBoard.put(Position.of(File.F, Rank.TWO), new Piece(new Pawn(Color.WHITE)));
        chessBoard.put(Position.of(File.F, Rank.ONE), new Piece(new King(Color.WHITE)));
        chessBoard.put(Position.of(File.G, Rank.FOUR), new Piece(new Queen(Color.WHITE)));
        chessBoard.put(Position.of(File.G, Rank.TWO), new Piece(new Pawn(Color.WHITE)));
        chessBoard.put(Position.of(File.H, Rank.THREE), new Piece(new Pawn(Color.WHITE)));

        assertAll(
                () -> assertThat(Score.of(chessBoard, Color.BLACK)).isEqualTo(new Score(20.0)),
                () -> assertThat(Score.of(chessBoard, Color.WHITE)).isEqualTo(new Score(19.5))
        );
    }

}
