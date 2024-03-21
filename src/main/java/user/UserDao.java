package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String SERVER = "localhost:3306"; // MySQL 서버 주소
    private static final String DATABASE = "chess"; // MySQL DATABASE 이름
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "ho-tea"; //  MySQL 서버 아이디
    private static final String PASSWORD = "990220"; // MySQL 서버 비밀번호

    public Connection getConnection() {
        // 드라이버 연결
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + OPTION, USERNAME, PASSWORD);
            connection.setAutoCommit(false);
            return connection;
        } catch (final SQLException e) {
            System.err.println("DB 연결 오류:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public List<User> findAll() {
        try (final var connection = getConnection()) {
            final var statement = connection.prepareStatement("select * from user");
            final var resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                var userId = resultSet.getString("user_id");
                var name = resultSet.getString("name");
                users.add(new User(userId, name));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public User findById(String userId) {
        try (final var connection = getConnection()) {
            final var statement = connection.prepareStatement("select * from user where user_id = ?");
            statement.setString(1, userId);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getString("user_id"),
                        resultSet.getString("name"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int save(User user) {
        try (final var connection = getConnection()) {
            final var statement = connection.prepareStatement("insert into user(user_id, name) values (?, ?)");
            statement.setString(1, user.userId());
            statement.setString(2, user.name());
            int id = statement.executeUpdate();
            connection.commit();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteById(String userId) {
        try (final var connection = getConnection()) {
            final var statement = connection.prepareStatement("delete from user where user_id = ?");
            statement.setString(1, userId);
            int id = statement.executeUpdate();
            connection.commit();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(User user) {
        try (final var connection = getConnection()) {
            final var statement = connection.prepareStatement("update user set name = ? where user_id = ?");
            statement.setString(1, user.name());
            statement.setString(2, user.userId());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() throws SQLException {
        getConnection().rollback();
        getConnection().commit();
    }
}
