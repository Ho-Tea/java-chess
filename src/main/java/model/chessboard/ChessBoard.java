package model.chessboard;

import dao.ChessDao;
import dao.ChessDaoImpl;
import dao.ChessDaoProxy;
import entity.ChessBoardEntity;
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
    private static final ChessDao chessDao = new ChessDaoProxy(new ChessDaoImpl());
    private final Map<Position, Piece> chessBoard;
    private final ChessState chessState;

    private ChessBoard(final Map<Position, Piece> chessBoard, final ChessState chessState) {
        this.chessBoard = chessBoard;
        this.chessState = chessState;
    }

    private ChessBoard(final Map<Position, Piece> chessBoard) {
        this(chessBoard, new ChessState());
    }

    public static ChessBoard load() {
        if (chessDao.isPiecesTableNotEmpty()) {
            ChessState chessState = ChessState.infuse(chessDao.findTurn(1L));
            return new ChessBoard(PieceEntityMapper.toChessBoard(chessDao.findAllPieces()), chessState);
        }
        throw new IllegalStateException("저장된 체스판이 존재하지 않습니다." + System.lineSeparator() + "새로운 체스판을 생성합니다.");
    }

    public static ChessBoard initialize() {
        chessDao.dropTables();
        chessDao.initializeTable();
        Map<Position, Piece> chessBoard = ChessBoardFactory.create();
        Long chessBoardId = chessDao.insertChessBoard(new ChessBoardEntity(null, Color.WHITE.name()));
        List<PieceEntity> pieceEntries = PieceEntityMapper.toPieceEntities(chessBoardId, chessBoard);
        pieceEntries.forEach(chessDao::insertPiece);
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
        save(chessState.nextTurn(), source, target);
        chessState.passTheTurn();
    }

    private void save(final Color turn, final Position source, final Position target) {
        PieceEntity sourceEntity = chessDao.findByRankAndFile(source.rank().index(), source.file().index());
        PieceEntity targetEntity = chessDao.findByRankAndFile(target.rank().index(), target.file().index());
        chessDao.updatePiece(targetEntity.id(), sourceEntity);
        chessDao.updatePiece(sourceEntity.id(), PieceEntityMapper.toSquarePieceEntity(targetEntity));
        chessDao.updateTurn(sourceEntity.chessBoardId(), turn.name());
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
            chessDao.dropTables();
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
