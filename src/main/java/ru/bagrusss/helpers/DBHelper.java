package ru.bagrusss.helpers;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

/**
 * Created by vladislav on 19.10.15.
 */

public final class DBHelper implements Helper {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db?characterEncoding=utf8";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";

    private static final int MAX_OPEN_PREPARED_STATEMENTS = 120;
    private static DBHelper mDBHelper;
    private final BasicDataSource mBasicDataSource;

    private static final int MIN_CONNECTIONS = 4;
    private static final int MAX_CONNECTIONS = 10;


    private DBHelper() {
        mBasicDataSource = new BasicDataSource();
        mBasicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        mBasicDataSource.setUsername(DB_USER);
        mBasicDataSource.setPassword(DB_PASS);
        mBasicDataSource.setUrl(DB_HOST);

        mBasicDataSource.setMinIdle(MIN_CONNECTIONS);
        mBasicDataSource.setMaxIdle(MAX_CONNECTIONS);
        mBasicDataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);
    }

    public static DBHelper getInstance() {
        DBHelper localInstance = mDBHelper;
        if (localInstance == null) {
            synchronized (DBHelper.class) {
                localInstance = mDBHelper;
                if (localInstance == null)
                    mDBHelper = localInstance = new DBHelper();
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
                                 List<?> params, ResultHandlet result) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
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
    public int runPreparedUpdate(@NotNull Connection connection, String sql, List<?> params) throws SQLException {
        int res = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            res = preparedStatement.executeUpdate();
        } finally {
            connection.close();
        }
        return res;
    }

}
