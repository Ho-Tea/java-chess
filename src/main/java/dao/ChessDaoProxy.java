package dao;

import entity.PieceEntity;

import java.util.List;

public class ChessDaoProxy implements ChessDao {
    private final ChessDao chessDao;
    private final boolean dbAvailable;

    public ChessDaoProxy(final ChessDao chessDao) {
        this.chessDao = chessDao;
        this.dbAvailable = chessDao.isConnectionFail();
    }

    @Override
    public boolean isConnectionFail() {
        if (!dbAvailable) {
            return true;
        }
        return false;
    }

    @Override
    public void initializeTable() {
        if (!dbAvailable) {
            System.out.println("데이터 베이스와의 연결에 실패하여 단순 콘솔 출력으로만 진행합니다.");
            return;
        }
        chessDao.initializeTable();
    }

    @Override
    public boolean isTableNotEmpty() {
        if (!dbAvailable) {
            return false;
        }
        return chessDao.isTableNotEmpty();
    }

    @Override
    public void insert(final PieceEntity pieceEntity) {
        if (!dbAvailable) {
            return;
        }
        chessDao.insert(pieceEntity);
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
            return new PieceEntity(1L, 1,1,"","");
        }
        return chessDao.findByRankAndFile(rank, file);
    }

    @Override
    public void update(final Long id, final PieceEntity pieceEntity) {
        if (!dbAvailable) {
            return;
        }
        chessDao.update(id, pieceEntity);
    }

    @Override
    public void deleteAll() {
        if (!dbAvailable) {
            return;
        }
        chessDao.deleteAll();
    }
}
