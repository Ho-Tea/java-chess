package dao;

import entity.PieceEntity;
import view.dto.PieceResponse;

import java.util.List;

public interface ChessDao {


    void initializeTable();

    boolean isTableEmpty();

    void insert(final PieceResponse pieceResponse);

    List<PieceEntity> findAllPieces();

    PieceEntity findByRankAndFile(final int rank, final int file);

    void update(final Long id, final PieceEntity pieceEntity);

    void deleteAll();
}
