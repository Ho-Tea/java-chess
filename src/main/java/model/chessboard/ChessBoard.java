package model.chessboard;

import dao.ChessDao;
import dao.ChessDaoImpl;
import entity.PieceEntity;
import mapper.PieceEntityMapper;
import model.direction.Destination;
import model.direction.Route;
import model.direction.WayPoints;
import model.piece.Color;
import model.piece.Piece;
import model.position.Position;
import model.score.Score;
import model.state.ChessState;

import java.util.List;
import java.util.Map;

public class ChessBoard {
    private static final ChessDao chessDao = new ChessDaoImpl();
    private final Map<Position, Piece> chessBoard;
    private final ChessState chessState;

    private ChessBoard(final Map<Position, Piece> chessBoard, final ChessState chessState) {
        this.chessBoard = chessBoard;
        this.chessState = chessState;
        chessDao.initializeTable();
    }

    private ChessBoard(final Map<Position, Piece> chessBoard) {
        this(chessBoard, new ChessState());
    }

    public static ChessBoard load() {
        if (chessDao.isTableNotEmpty()) {
            return new ChessBoard(PieceEntityMapper.toChessBoard(chessDao.findAllPieces()));
        }
        throw new IllegalStateException("저장된 체스판이 존재하지 않습니다.");
    }

    public static ChessBoard initialize() {
        Map<Position, Piece> chessBoard = ChessBoardFactory.create();
        if (chessDao.isTableNotEmpty()) {
            chessDao.deleteAll();
        }
        List<PieceEntity> pieceEntries = PieceEntityMapper.toPieceEntities(chessBoard);
        pieceEntries.forEach(chessDao::insert);
        return new ChessBoard(chessBoard);
    }

    public void proceedToTurn(final Position source, final Position target) {
        chessState.validateCheck(chessBoard);
        Piece sourcePiece = chessBoard.get(source);
        Piece targetPiece = chessBoard.get(target);
        chessState.checkTheTurn(sourcePiece);
        Route route = sourcePiece.findRoute(source, target);
        WayPoints wayPoints = WayPoints.of(chessBoard, route, target);
        Destination destination = new Destination(target, targetPiece);
        sourcePiece.validateMoving(wayPoints, destination);
        move(source, sourcePiece, destination);
        chessState.passTheTurn();
        apply(source, target);
    }

    public void apply(final Position source, final Position target) {
        PieceEntity sourceEntity = chessDao.findByRankAndFile(source.rank().index(), source.file().index());
        PieceEntity targetEntity = chessDao.findByRankAndFile(target.rank().index(), target.file().index());
        chessDao.update(targetEntity.id(), sourceEntity);
        chessDao.update(sourceEntity.id(), PieceEntityMapper.toSquarePieceEntity(targetEntity));
    }

    private void move(final Position source, final Piece sourcePiece, final Destination destination) {
        Piece targetPiece = new Piece(destination.target());
        try {
            sourcePiece.moveTo(destination);
            chessState.validateTriggerOfCheck(chessBoard);
        } catch (IllegalArgumentException e) {
            revoke(source, destination.target(), new Destination(destination.position(), targetPiece));
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void revoke(final Position source, final Piece sourcePiece, final Destination destination) {
        chessBoard.put(source, sourcePiece);
        chessBoard.put(destination.position(), destination.target());
    }

    public Score score(final Color color) {
        return Score.of(chessBoard, color);
    }

    public boolean checkMate() {
        if (chessState.checkMate(chessBoard)) {
            chessDao.deleteAll();
            return true;
        }
        return false;
    }

    public String winner() {
        if (chessState.checkMate(chessBoard)) {
            return chessState.winner().name();
        }
        return GameResult.DRAW.name();
    }

    public Map<Position, Piece> getChessBoard() {
        return Map.copyOf(chessBoard);
    }
}
