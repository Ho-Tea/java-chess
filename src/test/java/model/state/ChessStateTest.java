package model.state;

import model.piece.Color;
import model.piece.Piece;
import model.piece.role.King;
import model.piece.role.Pawn;
import model.piece.role.Rook;
import model.piece.role.Square;
import model.position.File;
import model.position.Position;
import model.position.Rank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChessStateTest {

    @Test
    @DisplayName("어떤 진영의 차례인지 알 수 있다.")
    void checkSameFaction() {
        ChessState chessState = new ChessState();
        assertThatThrownBy(() -> chessState.checkTheTurn(new Piece(new Pawn(Color.BLACK))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하얀색 진영의 기물을 이동해야 합니다.");
    }

    @Test
    @DisplayName("상대방에게 차례를 넘길 수 있다.")
    void passTheTurn() {
        ChessState chessState = new ChessState();
        chessState.passTheTurn();
        assertThatThrownBy(() -> chessState.checkTheTurn(new Piece(new Pawn(Color.WHITE))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검정색 진영의 기물을 이동해야 합니다.");
    }

    @Test
    @DisplayName("체크가 된 경우 체크를 해소할 수 있는 이동만 가능하다.")
    void validateCheck() {
        Map<Position, Piece> chessBoard = makeCheckChessBoard();
        ChessState chessState = new ChessState();
        assertThatThrownBy(() -> chessState.validateTriggerOfCheck(chessBoard))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 방향으로의 이동은 Check를 유발합니다.");
    }

    private Map<Position, Piece> makeCheckChessBoard() {
        Map<Position, Piece> chessBoard = new HashMap<>();
        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                chessBoard.put(Position.of(file, rank), new Piece(new Square()));
            }
        }
        chessBoard.put(Position.of(File.D, Rank.THREE), new Piece(new King(Color.WHITE)));
        chessBoard.put(Position.of(File.D, Rank.SEVEN), new Piece(new Rook(Color.BLACK)));
        chessBoard.put(Position.of(File.E, Rank.FOUR), new Piece(new Rook(Color.WHITE)));
        return chessBoard;
    }

    @DisplayName("킹이 잡힌 경우 체크메이트가 발생한다.")
    @Test
    void checkMate() {
        Map<Position, Piece> chessBoard = makeCheckMateChessBoard();
        ChessState chessState = new ChessState();
        chessState.validateCheck(makeCheckChessBoard());
        assertThat(chessState.checkMate(chessBoard)).isTrue();
    }

    private Map<Position, Piece> makeCheckMateChessBoard() {
        Map<Position, Piece> chessBoard = new HashMap<>();
        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                chessBoard.put(Position.of(file, rank), new Piece(new Square()));
            }
        }
        chessBoard.put(Position.of(File.H, Rank.TWO), new Piece(new King(Color.BLACK)));
        return chessBoard;
    }
}
