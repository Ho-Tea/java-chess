package view;

import view.dto.PieceInfo;

import java.util.Arrays;
import java.util.List;

public class OutputView {

    private static final int BOARD_SIZE = 8;
    private static final char EMPTY_SPACE = '.';

    private char[][] initializeChessBoard() {
        char[][] chessBoard = new char[BOARD_SIZE][BOARD_SIZE];
        for (char[] row : chessBoard) {
            Arrays.fill(row, EMPTY_SPACE);
        }
        return chessBoard;
    }

    public void printChessBoard(final List<PieceInfo> pieceInfos) {
        char[][] chessBoard = initializeChessBoard();
        pieceInfos.forEach(pieceInfo -> placePieceOnBoard(chessBoard, pieceInfo));
        printBoard(chessBoard);
    }

    public void printInitialGamePrompt() {
        System.out.println("체스 게임을 시작합니다.");
        System.out.println("> 게임 시작 : start");
        System.out.println("> 게임 종료 : end");
        System.out.println("> 게임 이동 : move source위치 target위치 - 예. move b2 b3");
    }

    private void placePieceOnBoard(final char[][] chessBoard, final PieceInfo pieceInfo) {
        int rowIndex = BOARD_SIZE - pieceInfo.rank();
        int columnIndex = pieceInfo.file() - 1;
        chessBoard[rowIndex][columnIndex] = pieceInfo.role();
    }

    private void printBoard(final char[][] chessBoard) {
        Arrays.stream(chessBoard)
              .map(String::valueOf)
              .forEach(System.out::println);
        System.out.println();
    }

    public void printScore(double score, String color) {
        System.out.printf("%s : %.1f 점", color, score);
        System.out.println();
    }

    public void printWinner(String color) {
        System.out.println("승리 : " + color);
    }

    public void printExceptionMessage(final String message) {
        System.out.println("[ERROR] " + message);
    }
}
