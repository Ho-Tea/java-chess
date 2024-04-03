package dao;

import db.DBConnection;
import entity.PieceEntity;
import view.dto.PieceResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChessDaoImpl implements ChessDao {
    private final Connection connection;

    public ChessDaoImpl() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void initializeTable() {
        if (!isTableExists()) {
            createTable();
        }
    }

    private boolean isTableExists() {
        return queryExecute("""
                        SELECT 1 FROM Information_schema.tables
                        WHERE table_schema = 'chess'
                        AND table_name = 'chess_pieces'
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.executeQuery(sql)
                               .next();
                });
    }

    private void createTable() {
        queryExecute("""
                        CREATE TABLE chess_pieces (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            rank_index INT NOT NULL,
                            file_index INT NOT NULL,
                            symbol VARCHAR(5) NOT NULL
                        )""",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    @Override
    public boolean isTableEmpty() {
        return queryExecute("SELECT COUNT(*) AS rowcount FROM chess_pieces",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.executeQuery(sql)
                               .next();
                });
    }

    @Override
    public void insert(final PieceResponse pieceResponse) {
        queryExecute("INSERT INTO chess_pieces (rank_index, file_index, symbol) VALUES (?, ?, ?)",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, pieceResponse.rank());
                    pstmt.setInt(2, pieceResponse.file());
                    pstmt.setString(3, String.valueOf(pieceResponse.role()));
                    return pstmt.executeUpdate();
                });
    }

    @Override
    public List<PieceEntity> findAllPieces() {
        return queryExecute("SELECT * FROM chess_pieces",
                sql -> {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    return toPieceEntities(rs);
                });
    }

    @Override
    public PieceEntity findByRankAndFile(final int rank, final int file) {
        return queryExecute("SELECT * FROM chess_pieces WHERE rank_index = ? AND file_index = ?",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, rank);
                    pstmt.setInt(2, file);
                    return toPieceEntities(pstmt.executeQuery()).get(0);
                });
    }

    private List<PieceEntity> toPieceEntities(final ResultSet resultSet) throws SQLException {
        List<PieceEntity> pieces = new ArrayList<>();
        while (resultSet.next()) {
            pieces.add(new PieceEntity(
                    resultSet.getLong("id"),
                    resultSet.getInt("rank_index"),
                    resultSet.getInt("file_index"),
                    resultSet.getString("symbol")));
        }
        return pieces;
    }

    @Override
    public void update(final Long id, final PieceEntity pieceEntity) {
        queryExecute("UPDATE chess_pieces SET symbol = ? WHERE id = ?",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, pieceEntity.symbol());
                    pstmt.setLong(2, id);
                    return pstmt.executeUpdate();
                });
    }

    @Override
    public void deleteAll() {
        queryExecute("DELETE FROM chess_pieces;",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.executeUpdate(sql);
                });
    }

    private <T> T queryExecute(final String sql, final SqlExecutor<T> sqlExecutor) {
        try {
            return sqlExecutor.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
