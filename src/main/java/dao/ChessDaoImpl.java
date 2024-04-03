package dao;

import db.DBConnection;
import entity.PieceEntity;

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
                            color ENUM('BLACK', 'WHITE', 'UN_COLORED'),
                            role ENUM('PAWN', 'BISHOP', 'KNIGHT', 'KING', 'QUEEN', 'ROOK', 'SQUARE')
                        )""",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    @Override
    public boolean isTableNotEmpty() {
        return queryExecute("SELECT COUNT(*) AS rowcount FROM chess_pieces",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.executeQuery(sql)
                               .next();
                });
    }

    @Override
    public void insert(final PieceEntity pieceEntity) {
        queryExecute("INSERT INTO chess_pieces (rank_index, file_index, color, role) VALUES (?, ?, ?, ?)",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, pieceEntity.rank());
                    pstmt.setInt(2, pieceEntity.file());
                    pstmt.setString(3, pieceEntity.color());
                    pstmt.setString(4, pieceEntity.role());
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
                    resultSet.getString("color"),
                    resultSet.getString("role")));
        }
        return pieces;
    }

    @Override
    public void update(final Long id, final PieceEntity pieceEntity) {
        queryExecute("UPDATE chess_pieces SET color = ?, role = ? WHERE id = ?",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, pieceEntity.color());
                    pstmt.setString(2, pieceEntity.role());
                    pstmt.setLong(3, id);
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
