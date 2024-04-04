package controller;

import dto.PieceDto;
import mapper.PieceDtoMapper;
import model.chessboard.ChessBoard;
import model.piece.Color;
import model.position.Position;
import view.GameCommand;
import view.InputView;
import view.OutputView;
import view.dto.GameProceedRequest;

import java.util.List;

public class Controller {
    private final OutputView outputView;

    public Controller() {
        outputView = new OutputView();
    }

    public void execute() {
        outputView.printGamePrompt();
        GameCommand gameCommand = initialGameCommand();
        ChessBoard chessBoard = generateChessBoard(gameCommand);
        while (gameCommand != GameCommand.END && !chessBoard.checkMate()) {
            List<PieceDto> pieceDto = PieceDtoMapper.toPieceDto(chessBoard);
            outputView.printChessBoard(pieceDto);
            gameCommand = play(chessBoard);
        }
        chessBoardWinner(chessBoard);
        chessBoardScore(chessBoard);
    }

    private GameCommand initialGameCommand() {
        try {
            return InputView.inputInitialGameCommand();
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return initialGameCommand();
        }
    }

    private ChessBoard generateChessBoard(final GameCommand gameCommand) {
        try {
            if (gameCommand == GameCommand.LOAD) {
                return ChessBoard.load();
            }
        } catch (IllegalStateException e) {
            outputView.printExceptionMessage(e.getMessage());
        }
        return ChessBoard.initialize();
    }

    private GameCommand play(final ChessBoard chessBoard) {
        try {
            GameProceedRequest gameProceedRequest = InputView.inputGameProceedCommand();
            GameCommand gameCommand = gameProceedRequest.gameCommand();
            if (gameCommand != GameCommand.MOVE && gameCommand != GameCommand.END) {
                throw new IllegalArgumentException("현재 진행중인 게임이 존재합니다.");
            }
            if (gameCommand == GameCommand.MOVE) {
                controlChessBoard(chessBoard, gameProceedRequest);
            }
            return gameCommand;
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

    private void chessBoardWinner(final ChessBoard chessBoard) {
        try {
            GameCommand gameCommand = InputView.inputGameStatusCommand();
            if (gameCommand == GameCommand.STATUS) {
                outputView.printWinner(chessBoard.winner());
            }
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            chessBoardWinner(chessBoard);
        }
    }

    private void chessBoardScore(final ChessBoard chessBoard) {
        outputView.printScore(chessBoard.score(Color.BLACK).value(), Color.BLACK.name());
        outputView.printScore(chessBoard.score(Color.WHITE).value(), Color.WHITE.name());
    }
}
