package dao;

import entity.PieceEntity;

import java.util.List;

public interface ChessDao {


    void initializeTable();

    boolean isTableNotEmpty();

    void insert(final PieceEntity pieceEntity);

    List<PieceEntity> findAllPieces();

    PieceEntity findByRankAndFile(final int rank, final int file);

    void update(final Long id, final PieceEntity pieceEntity);

    void deleteAll();
}
