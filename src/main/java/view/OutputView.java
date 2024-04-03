package view;

import dto.PieceDto;

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

    public void printChessBoard(final List<PieceDto> pieceRespons) {
        char[][] chessBoard = initializeChessBoard();
        pieceRespons.forEach(pieceInfo -> placePieceOnBoard(chessBoard, pieceInfo));
        printBoard(chessBoard);
    }

    public void printGamePrompt() {
        System.out.println("체스 게임을 시작합니다.");
        System.out.println("> 게임 시작 : start");
        System.out.println("> 게임 불러오기 : load");
        System.out.println("> 게임 종료 : end");
        System.out.println("> 게임 이동 : move source위치 target위치 - 예. move b2 b3");
    }

    private void placePieceOnBoard(final char[][] chessBoard, final PieceDto pieceDto) {
        int rowIndex = BOARD_SIZE - pieceDto.rank();
        int columnIndex = pieceDto.file() - 1;
        chessBoard[rowIndex][columnIndex] = pieceDto.role();
    }

    private void printBoard(final char[][] chessBoard) {
        Arrays.stream(chessBoard)
              .map(String::valueOf)
              .forEach(System.out::println);
        System.out.println();
    }

    public void printScore(final double score, String color) {
        System.out.printf("%s : %.1f 점", color, score);
        System.out.println();
    }

    public void printWinner(final String winner) {
        System.out.println("게임 결과  : " + winner);
    }

    public void printExceptionMessage(final String message) {
        System.out.println("[ERROR] " + message);
    }
}
