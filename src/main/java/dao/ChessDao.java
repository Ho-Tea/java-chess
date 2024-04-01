package dao;

import view.dto.ChessPiece;
import view.dto.PieceInfo;

import java.util.List;

public interface ChessDao {

    void insertPiece(final PieceInfo pieceInfo);

    boolean isInitITable();

    List<ChessPiece> findAllPieces();

    ChessPiece findByRankAndFile(final int rank, final int file);

    void update(final Long id, final ChessPiece chessPiece);

    void deleteAllPieces();

    void initializeChessTable();
}
