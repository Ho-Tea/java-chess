package controller;

import model.chessboard.ChessBoard;
import model.piece.Color;
import model.position.Position;
import util.PieceInfoMapper;
import view.GameCommand;
import view.InputView;
import view.OutputView;
import view.dto.GameProceedRequest;
import view.dto.PieceInfo;

import java.util.List;

public class Controller {
    private final OutputView outputView;

    public Controller() {
        outputView = new OutputView();
    }

    public void execute() {
        outputView.printInitialGamePrompt();
        GameCommand gameCommand = initGameCommand();
        ChessBoard chessBoard = new ChessBoard();
        while (gameCommand != GameCommand.END && !chessBoard.checkMate()) {
            List<PieceInfo> pieceInfos = PieceInfoMapper.toPieceInfo(chessBoard);
            outputView.printChessBoard(pieceInfos);
            gameCommand = play(chessBoard);
        }
        finish(chessBoard);
    }

    private GameCommand initGameCommand() {
        try {
            return InputView.inputInitialGameCommand();
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return initGameCommand();
        }
    }

    private GameCommand play(final ChessBoard chessBoard) {
        try {
            GameProceedRequest gameProceedRequest = InputView.inputGameProceedCommand();
            if (gameProceedRequest.gameCommand() == GameCommand.START) {
                throw new IllegalArgumentException("이미 진행중인 게임이 존재합니다.");
            }
            if (gameProceedRequest.gameCommand() == GameCommand.MOVE) {
                controlChessBoard(chessBoard, gameProceedRequest);
            }
            return gameProceedRequest.gameCommand();
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return play(chessBoard);
        }
    }

    private void controlChessBoard(final ChessBoard chessBoard, final GameProceedRequest gameProceedRequest) {
        Position source = Position.from(gameProceedRequest.sourcePosition());
        Position destination = Position.from(gameProceedRequest.targetPosition());
        chessBoard.proceedToTurn(source, destination);
    }

    private void finish(final ChessBoard chessBoard) {
        try {
            GameCommand gameCommand = InputView.inputGameStatusCommand();
            if (gameCommand == GameCommand.STATUS) {
                outputView.printWinner(chessBoard.winner().name());
                outputView.printScore(chessBoard.score(Color.BLACK).value(), Color.BLACK.name());
                outputView.printScore(chessBoard.score(Color.WHITE).value(), Color.WHITE.name());
            }
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            finish(chessBoard);
        }
    }
}
