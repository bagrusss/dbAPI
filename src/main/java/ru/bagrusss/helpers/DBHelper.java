package ru.bagrusss.helpers;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Map;

/**
 * Created by vladislav on 19.10.15.
 */

public final class DBHelper implements Helper {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";
    private static final int MAX_OPEN_PREPARED_STATEMENTS = 100;
    private static DBHelper s_dbHelper;
    private final BasicDataSource mBasicDataSource;

    private DBHelper() {
        mBasicDataSource = new BasicDataSource();
        mBasicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        mBasicDataSource.setUsername(DB_USER);
        mBasicDataSource.setPassword(DB_PASS);
        mBasicDataSource.setUrl(DB_HOST);

        mBasicDataSource.setMinIdle(4);
        mBasicDataSource.setMaxIdle(10);
        mBasicDataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);
    }

    public static DBHelper getInstance() {
        DBHelper localInstance = s_dbHelper;
        if (localInstance == null) {
            synchronized (DBHelper.class) {
                localInstance = s_dbHelper;
                if (localInstance == null)
                    s_dbHelper = localInstance = new DBHelper();
            }
        }
        return localInstance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return mBasicDataSource.getConnection();
    }

    @Override
    public void runQuery(@NotNull Connection connection, String sql, ResultHandlet resultHandlet) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultHandlet.handle(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    @Override
    public void runPreparedQuery(@NotNull Connection connection, String sql,
                                 Map<Integer, Object> params, ResultHandlet result) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Integer i : params.keySet()) {
                preparedStatement.setObject(i, params.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            result.handle(resultSet);
            resultSet.close();
        } finally {
            connection.close();
        }
    }

    @Override
    public int runUpdate(@NotNull Connection connection, String sql) throws SQLException {
        int updated = 0;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            updated = statement.getUpdateCount();
        } finally {
            connection.close();
        }
        return updated;
    }

    @Override
    public void runPreparedUpdate(@NotNull Connection connection, String sql, Map<Integer, String> params) {

    }
}
