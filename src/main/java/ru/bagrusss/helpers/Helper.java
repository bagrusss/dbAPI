package ru.bagrusss.helpers;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by vladislav on 20.10.15.
 */

public interface Helper {

    String[] TABLES = new String[]{" User ", " Thread ", " Post ", " Forum ", " Followers ", " Subscriptions "};

    String TABLE_USER = TABLES[0];
    String TABLE_THREAD = TABLES[1];
    String TABLE_POST = TABLES[2];
    String TABLE_FORUM = TABLES[3];
    String TABLE_FOLLOWERS = TABLES[4];
    String TABLE_SUBSCRIPTIONS = TABLES[5];

    Connection getConnection() throws SQLException;

    void runQuery(@NotNull Connection connection, String sql, ResultHandlet resultHandlet) throws SQLException;

    void runPreparedQuery(@NotNull Connection connection, String sql, Map<Integer, Object> params, ResultHandlet resultHandlet) throws SQLException;

    int runUpdate(@NotNull Connection connection, String sql) throws SQLException;

    void runPreparedUpdate(@NotNull Connection connection, String sql, Map<Integer, String> params) throws SQLException;
}
