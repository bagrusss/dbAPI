package ru.bagrusss.helpers;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */

public final class DBHelper implements Helper {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db?characterEncoding=utf8&autoreconnect=true";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";

    private static final int MAX_OPEN_PREPARED_STATEMENTS = 120;
    private static final int MIN_CONNECTIONS = 5;
    private static final int MAX_CONNECTIONS = 8;
    private static final Logger LOGGER = Logger.getLogger(DBHelper.class.getName());
    private static DBHelper mDBHelper;
    private final BasicDataSource mBasicDataSource;

    private DBHelper() {
        mBasicDataSource = new BasicDataSource();
        mBasicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        mBasicDataSource.setUsername(DB_USER);
        mBasicDataSource.setPassword(DB_PASS);
        mBasicDataSource.setUrl(DB_HOST);

        mBasicDataSource.setMinIdle(MIN_CONNECTIONS);
        mBasicDataSource.setMaxIdle(MAX_CONNECTIONS);
        mBasicDataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);
        try {
            this.runUpdate(getConnection(), "SET sql_mode='NO_UNSIGNED_SUBTRACTION';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        int active = mBasicDataSource.getNumActive();
        if (mBasicDataSource.getNumActive() > 4) {
            LOGGER.log(Level.INFO, "More than 4 connettions " + active);
        }
        return mBasicDataSource.getConnection();
    }

    @Nullable
    @Override
    public <T> T runTypedQuery(Connection connection, String sql, TResultHandler<T> tHandler) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return tHandler.handle(resultSet);
        }
    }

    @Override
    public <T> T runTypedPreparedQuery(Connection connection, String sql, List<?> params, TResultHandler<T> tHandler) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery(sql)) {
                return tHandler.handle(resultSet);
            }
        }
    }

    @Override
    public void runQuery(@NotNull Connection connection, String sql, ResultHandler resultHandlet) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultHandlet.handle(resultSet);
        }
    }

    @Override
    public void runPreparedQuery(@NotNull Connection connection, String sql,
                                 List<?> params, ResultHandler result) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            result.handle(resultSet);
            resultSet.close();
        }
    }

    @Override
    public int runUpdate(@NotNull Connection connection, String sql) throws SQLException {
        int updated;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            updated = statement.getUpdateCount();
        }
        return updated;
    }

    @Override
    public int runPreparedUpdate(@NotNull Connection connection, String sql, List<?> params) throws SQLException {
        int res;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            res = preparedStatement.executeUpdate();
        }
        return res;
    }

    @Override
    public long updateAndGetID(@NotNull Connection connection, String sql) throws SQLException {
        long id = 0;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            try (ResultSet res = statement.getGeneratedKeys()) {
                if (res.next()) {
                    id = res.getLong(1);
                }
            }
        }
        return id;
    }

    @Override
    public long preparedInsertAndGetKeys(@NotNull Connection connection, String sql, List<?> params) throws SQLException {
        long id = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            preparedStatement.executeUpdate();
            try (ResultSet result = preparedStatement.getGeneratedKeys()) {
                if (result.next())
                    id = result.getLong(1);
            }
        }
        return id;
    }

    @Override
    public void preparedInsertAndGetKeys(@NotNull Connection connection, String sql, List<?> params, ResultHandler gk) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            for (Object par : params) {
                preparedStatement.setObject(i++, par);
            }
            preparedStatement.executeUpdate();
            try (ResultSet result = preparedStatement.getGeneratedKeys()) {
                gk.handle(result);
            }
        }
    }
}

