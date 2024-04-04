package dao;

import entity.ChessBoardEntity;
import entity.PieceEntity;

import java.util.List;

public interface ChessDao {
    boolean isConnection();

    void initializeTable();

    void dropTables();

    boolean isPiecesTableNotEmpty();

    void insertPiece(final PieceEntity pieceEntity);

    Long insertChessBoard(final ChessBoardEntity chessBoard);

    String findTurn(final Long chessBoardId);

    List<PieceEntity> findAllPieces();

    PieceEntity findByRankAndFile(final int rank, final int file);

    void updatePiece(final Long id, final PieceEntity pieceEntity);

    void updateTurn(final Long id, final String turn);
}
