package dao;

import view.dto.ChessPiece;
import model.position.File;
import model.position.Rank;
import view.dto.PieceInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChessDaoImpl implements ChessDao {
    private static final String SERVER = "localhost:13306";
    private static final String DATABASE = "chess";
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private Connection connection;

    public ChessDaoImpl() {
        this.connection = getConnection();
    }

    private Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + OPTION, USERNAME, PASSWORD);
            return connection;
        } catch (final SQLException e) {
            System.err.println("DB 연결 오류:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void insertPiece(PieceInfo pieceInfo) {
        String sql = "INSERT INTO chess_pieces (rank_index, file_index, symbol) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pieceInfo.rank());
            pstmt.setInt(2, pieceInfo.file());
            pstmt.setString(3, String.valueOf(pieceInfo.role()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeChessTable() {
        try {
            if (!tableExists()) {
                createChessPiecesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean tableExists() throws SQLException {
        String sql = """
                SELECT 1 FROM Information_schema.tables
                WHERE table_schema = 'chess'
                AND table_name = 'chess_pieces'
                """;
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(sql).next();
        }
    }

    private void createChessPiecesTable() throws SQLException {
        String sql = """
                CREATE TABLE chess_pieces (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    rank_index INT NOT NULL,
                    file_index INT NOT NULL,
                    symbol VARCHAR(5) NOT NULL
                )""";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public boolean isInitITable() {
        try {
            return isTableEmpty("chess_pieces");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }

    private boolean isTableEmpty(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) AS rowcount FROM " + tableName;
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt("rowcount");
                System.out.println(count);
                return count == 0;
            }
        }
        return true;
    }

    @Override
    public List<ChessPiece> findAllPieces() {
        List<ChessPiece> pieces = new ArrayList<>();
        String sql = "SELECT * FROM chess_pieces";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pieces.add(new ChessPiece(
                        rs.getLong("id"),
                        Rank.fromIndex(rs.getInt("rank_index")),
                        File.fromIndex(rs.getInt("file_index")),
                        rs.getString("symbol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pieces;
    }

    @Override
    public ChessPiece findByRankAndFile(int rank, int file) {
        List<ChessPiece> pieces = new ArrayList<>();
        String sql = "SELECT * FROM chess_pieces WHERE rank_index = ? AND file_index = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, rank);
            pstmt.setInt(2, file);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pieces.add(new ChessPiece(
                        rs.getLong("id"),
                        Rank.fromIndex(rs.getInt("rank_index")),
                        File.fromIndex(rs.getInt("file_index")),
                        rs.getString("symbol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pieces.get(0);
    }

    @Override
    public void update(final Long id, final ChessPiece chessPiece) {
        String sql = "UPDATE chess_pieces SET symbol = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chessPiece.symbol());
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllPieces() {
        String sqlDelete = "DELETE FROM chess_pieces;";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlDelete);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
