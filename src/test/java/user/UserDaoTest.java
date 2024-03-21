package user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {
    private UserDao userDao;

    @BeforeEach
    void setUp(){
        userDao = new UserDao();
    }

    @AfterEach
    void close() throws SQLException {
        userDao.rollback();
    }

    @Test
    void getConnection() {
        final var connection = userDao.getConnection();

        assertThat(connection).isNotNull();
    }

    @Test
    void findAll() {
        final var users = userDao.findAll();

        assertThat(users).contains(
                new User("pobiconan", "pobi"),
                new User("sugarbrown", "brown")
        );
    }

    @Test
    void findById() {
        final var users = userDao.findById("pobiconan");

        assertThat(users).isEqualTo(new User("pobiconan", "pobi"));
    }

    @Test
    void save(){
        User user = new User("2", "hotea");
        userDao.save(user);
        assertThat(userDao.findById("2")).isEqualTo(user);
    }

    @Test
    void deleteById(){
        userDao.deleteById("2");
        assertThat(userDao.findById("2")).isNull();
    }

    @Test
    void update(){
        userDao.update(new User("pobiconan", "pobe"));
        assertThat(userDao.findById("pobiconan").name()).isEqualTo("pobe");
    }
}
