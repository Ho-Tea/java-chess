package dao;

import java.sql.SQLException;

@FunctionalInterface
interface SqlExecutor<T> {
    T execute(String query) throws SQLException;
}
