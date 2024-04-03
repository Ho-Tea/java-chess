package dao;

import java.sql.SQLException;

@FunctionalInterface
interface SqlExecutor<T> {
    T execute(final String query) throws SQLException;
}
