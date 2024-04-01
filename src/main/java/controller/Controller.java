package controller;

import dao.ChessDao;
import dao.ChessDaoImpl;
import model.chessboard.ChessBoard;
import model.piece.Color;
import model.position.Position;
import util.PieceInfoMapper;
import view.GameCommand;
import view.InputView;
import view.OutputView;
import view.dto.ChessPiece;
import view.dto.GameProceedRequest;
import view.dto.PieceInfo;

import java.util.List;

public class Controller {
    private final OutputView outputView;
    private final ChessDao chessDao;

    public Controller() {
        outputView = new OutputView();
        chessDao = new ChessDaoImpl();
    }

    public void command() {
        chessDao.initializeChessTable();
        ChessBoard chess = new ChessBoard();
        if (chessDao.isInitITable()) {
            List<PieceInfo> pieceInfos = PieceInfoMapper.toPieceInfo(chess);
            pieceInfos.forEach(chessDao::insertPiece);
            outputView.printInitialGamePrompt();
            execute(chess);
            return;
        }
        outputView.printAlreadyExistGame();
        chess = PieceInfoMapper.toChessBoard(chessDao.findAllPieces());
        execute(chess);
    }

    public void execute(ChessBoard chessBoard) {
        GameCommand gameCommand = initGameCommand();
        while (gameCommand != GameCommand.END && !chessBoard.checkMate()) {
            List<PieceInfo> pieceInfos = PieceInfoMapper.toPieceInfo(chessBoard);
            outputView.printChessBoard(pieceInfos);
            gameCommand = play(chessBoard);
        }
        finish(chessBoard);
        if (chessBoard.checkMate()) {
            chessDao.deleteAllPieces();
        }
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
            if (gameProceedRequest.gameCommand() == GameCommand.END) {
                return GameCommand.END;
            }
            Position sourcePosition = Position.from(gameProceedRequest.sourcePosition());
            Position targetPosition = Position.from(gameProceedRequest.targetPosition());
            ChessPiece sourceChess = chessDao.findByRankAndFile(sourcePosition.rank().index(), sourcePosition.file().index());
            ChessPiece targetChess = chessDao.findByRankAndFile(targetPosition.rank().index(), targetPosition.file().index());
            chessDao.update(targetChess.id(), sourceChess);
            chessDao.update(sourceChess.id(), new ChessPiece(targetChess.id(), targetChess.rank(), targetChess.file(), "."));
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
