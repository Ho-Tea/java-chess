package dao;

import db.DBConnection;
import entity.ChessBoardEntity;
import entity.PieceEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChessDaoImpl implements ChessDao {
    private final Connection connection;

    public ChessDaoImpl() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean isConnection() {
        if (Objects.isNull(connection)) {
            return false;
        }
        return true;
    }

    @Override
    public void initializeTable() {
        if (!isChessBoardTableExists()) {
            createChessBoard();
        }
        if (!isChessPiecesTableExists()) {
            createChessPieces();
        }
    }

    private boolean isChessBoardTableExists() {
        return queryExecute("""
                        SELECT 1 FROM Information_schema.tables
                        WHERE table_schema = 'chess'
                        AND table_name = 'chess_board'
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    ResultSet resultSet = stmt.executeQuery(sql);
                    return resultSet.next();
                });
    }

    private boolean isChessPiecesTableExists() {
        return queryExecute("""
                        SELECT 1 FROM Information_schema.tables
                        WHERE table_schema = 'chess'
                        AND table_name = 'chess_pieces'
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    ResultSet resultSet = stmt.executeQuery(sql);
                    return resultSet.next();
                });
    }

    private void createChessBoard() {
        queryExecute("""
                        CREATE TABLE chess_board (
                            chess_board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            turn ENUM('WHITE', 'BLACK') NOT NULL
                            );                    
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    private void createChessPieces() {
        queryExecute("""
                        CREATE TABLE chess_pieces (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            chess_board_id BIGINT,
                            rank_index INT NOT NULL,
                            file_index INT NOT NULL,
                            color ENUM('BLACK', 'WHITE', 'UN_COLORED'),
                            role ENUM('PAWN', 'BISHOP', 'KNIGHT', 'KING', 'QUEEN', 'ROOK', 'SQUARE'),
                            FOREIGN KEY (chess_board_id) REFERENCES chess_board(chess_board_id)                            
                        )""",
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    @Override
    public void dropTables() {
        dropChessPieces();
        dropChessBoard();
    }

    private void dropChessBoard() {
        queryExecute("""
                        DROP TABLE IF EXISTS chess_board;
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    private void dropChessPieces() {
        queryExecute("""
                        DROP TABLE IF EXISTS chess_pieces;
                        """,
                sql -> {
                    Statement stmt = connection.createStatement();
                    return stmt.execute(sql);
                });
    }

    @Override
    public boolean isPiecesTableNotEmpty() {
        if (isChessPiecesTableExists()) {
            return queryExecute("SELECT COUNT(*) AS rowcount FROM chess_pieces",
                    sql -> {
                        Statement stmt = connection.createStatement();
                        return stmt.executeQuery(sql)
                                   .next();
                    });
        }
        return false;
    }

    @Override
    public Long insertChessBoard(final ChessBoardEntity chessBoardentity) {
        return queryExecute("INSERT INTO chess_board (turn) VALUES (?)",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, chessBoardentity.turn());
                    pstmt.executeUpdate();
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    generatedKeys.next();
                    return generatedKeys.getLong(1);
                });
    }

    @Override
    public String findTurn(final Long chessBoardId) {
        return queryExecute("SELECT turn FROM chess_board WHERE chess_board_id = ?",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setLong(1, chessBoardId);
                    ResultSet resultSet = pstmt.executeQuery();
                    resultSet.next();
                    return resultSet.getString("turn");
                });
    }

    @Override
    public void insertPiece(final PieceEntity pieceEntity) {
        queryExecute("INSERT INTO chess_pieces (chess_board_id, rank_index, file_index, color, role) VALUES (?, ?, ?, ?, ?)",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setLong(1, pieceEntity.chessBoardId());
                    pstmt.setInt(2, pieceEntity.rank());
                    pstmt.setInt(3, pieceEntity.file());
                    pstmt.setString(4, pieceEntity.color());
                    pstmt.setString(5, pieceEntity.role());
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
                    resultSet.getLong("chess_board_id"),
                    resultSet.getInt("rank_index"),
                    resultSet.getInt("file_index"),
                    resultSet.getString("color"),
                    resultSet.getString("role")));
        }
        return pieces;
    }

    @Override
    public void updatePiece(final Long id, final PieceEntity pieceEntity) {
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
    public void updateTurn(final Long id, final String turn) {
        queryExecute("UPDATE chess_board SET turn = ? WHERE chess_board_id = ?",
                sql -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, turn);
                    pstmt.setLong(2, id);
                    return pstmt.executeUpdate();
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
