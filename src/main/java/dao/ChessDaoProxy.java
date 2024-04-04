package dao;

import entity.ChessBoardEntity;
import entity.PieceEntity;

import java.util.List;

public class ChessDaoProxy implements ChessDao {
    private final ChessDao chessDao;
    private final boolean dbAvailable;

    public ChessDaoProxy(final ChessDao chessDao) {
        this.chessDao = chessDao;
        this.dbAvailable = chessDao.isConnection();
    }

    @Override
    public boolean isConnection() {
        return false;
    }

    @Override
    public void initializeTable() {
        if (!dbAvailable) {
            return;
        }
        chessDao.initializeTable();
    }

    @Override
    public void dropTables() {
        if (!dbAvailable) {
            return;
        }
        chessDao.dropTables();
    }

    @Override
    public boolean isPiecesTableNotEmpty() {
        if (!dbAvailable) {
            return false;
        }
        return chessDao.isPiecesTableNotEmpty();
    }

    @Override
    public void insertPiece(final PieceEntity pieceEntity) {
        if (!dbAvailable) {
            return;
        }
        chessDao.insertPiece(pieceEntity);
    }

    @Override
    public Long insertChessBoard(final ChessBoardEntity chessBoard) {
        if (!dbAvailable) {
            return null;
        }
        return chessDao.insertChessBoard(chessBoard);
    }

    @Override
    public String findTurn(final Long chessBoardId) {
        if (!dbAvailable) {
            return null;
        }
        return chessDao.findTurn(chessBoardId);
    }

    @Override
    public List<PieceEntity> findAllPieces() {
        if (!dbAvailable) {
            return List.of();
        }
        return chessDao.findAllPieces();
    }

    @Override
    public PieceEntity findByRankAndFile(final int rank, final int file) {
        if (!dbAvailable) {
            return new PieceEntity(1L, 1L, 1, 1, "", "");
        }
        return chessDao.findByRankAndFile(rank, file);
    }

    @Override
    public void updatePiece(final Long id, final PieceEntity pieceEntity) {
        if (!dbAvailable) {
            return;
        }
        chessDao.updatePiece(id, pieceEntity);
    }

    @Override
    public void updateTurn(final Long id, final String color) {
        if (!dbAvailable) {
            return;
        }
        chessDao.updateTurn(id, color);
    }
}
