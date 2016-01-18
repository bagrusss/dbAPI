package ru.bagrusss.helpers;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public final class DBHelper {

    public static final String[] TABLES = new String[]{" `User` ", " `Thread` ", " `Post` ",
            " `Forum` ", " `Followers` ", " `Subscriptions` "};

    public static final String TABLE_USER = TABLES[0];
    public static final String TABLE_THREAD = TABLES[1];
    public static final String TABLE_POST = TABLES[2];
    public static final String TABLE_FORUM = TABLES[3];
    public static final String TABLE_FOLLOWERS = TABLES[4];
    public static final String TABLE_SUBSCRIPTIONS = TABLES[5];

    public static final String FOLLOWERS_QUERY = "SELECT following_email FROM Followers FORCE INDEX (`PRIMARY`) WHERE follower_email=";
    public static final String FOLLOWING_QUERY = "SELECT follower_email FROM Followers FORCE INDEX (following_follower) WHERE following_email=";
    public static final String SUBSCRIPTIONS_QUERY = "SELECT thread_id FROM Subscriptions WHERE user_email=";

    public static final String USER_DETAILS_QUERY = "SELECT * FROM `User` WHERE email=";
    public static final String FORUM_DETAILS_QUERY = "SELECT * FROM `Forum` WHERE short_name=";

    public static final String THREAD_DETAILS_QUERY = "SELECT `id`, likes, dislikes, message, title, slug, user_email, forum, " +
            "isDeleted, isClosed, posts, likes-CAST(dislikes AS SIGNED) points, " +
            "DATE_FORMAT(`date`, '%Y-%m-%d %H:%i:%s') dt FROM `Thread` WHERE `id`=";

    public static final String POST_DETAILS_QUERY = "SELECT id, isApproved, isDeleted, isEdited, isHighlighted, isSpam, " +
            "message, likes, dislikes, thread_id, user_email, forum_short_name, parent, " +
            "likes-CAST(dislikes AS SIGNED) pointsDATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd FROM" + TABLE_POST + "WHERE `id`=";

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db?characterEncoding=utf8&autoreconnect=true";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final int MAX_OPEN_PREPARED_STATEMENTS = 120;
    private static final int MIN_CONNECTIONS = 5;
    private static final int MAX_CONNECTIONS = 8;
    private static final Logger LOGGER = Logger.getLogger(DBHelper.class.getName());

    private static DBHelper mDBHelper;
    private final BasicDataSource mBasicDataSource;

    private DBHelper() {
        mBasicDataSource = new BasicDataSource();
        mBasicDataSource.setDriverClassName(DRIVER);
        mBasicDataSource.setUsername(DB_USER);
        mBasicDataSource.setPassword(DB_PASS);
        mBasicDataSource.setUrl(DB_HOST);

        mBasicDataSource.setMinIdle(MIN_CONNECTIONS);
        mBasicDataSource.setMaxIdle(MAX_CONNECTIONS);
        mBasicDataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);
        try (Connection connection = getConnection()) {
            runUpdate(connection, "SET sql_mode='NO_UNSIGNED_SUBTRACTION'");
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

    public PreparedStatement prepareStatementThreadDetails(Connection connection) throws SQLException {
        return connection.prepareStatement(THREAD_DETAILS_QUERY + '?');
    }

    public PreparedStatement preparedStatementUserDetails(Connection connection) throws SQLException {
        return connection.prepareStatement(USER_DETAILS_QUERY + '?');
    }

    public PreparedStatement preparedStatementForumDetails(Connection connection) throws SQLException {
        return connection.prepareStatement(FORUM_DETAILS_QUERY + '?');
    }

    public PreparedStatement getPreparedStatementFollowing(Connection connection) throws SQLException {
        return connection.prepareStatement(FOLLOWING_QUERY + '?');
    }

    public PreparedStatement getPreparedStatementFollowers(Connection connection) throws SQLException {
        return connection.prepareStatement(FOLLOWERS_QUERY + '?');
    }

    public PreparedStatement getPreparedStatementSubscript(Connection connection) throws SQLException {
        return connection.prepareStatement(SUBSCRIPTIONS_QUERY + '?');
    }

    public Connection getConnection() throws SQLException {
        int active = mBasicDataSource.getNumActive();
        if (mBasicDataSource.getNumActive() > 4) {
            LOGGER.log(Level.INFO, "More than 4 connettions " + active);
        }
        return mBasicDataSource.getConnection();
    }


    public <T> T runTypedQuery(Connection connection, String sql, TResultHandler<T> tHandler) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return tHandler.handle(resultSet);
        }
    }

    public void runQuery(@NotNull Connection connection, String sql, ResultHandler resultHandlet) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultHandlet.handle(resultSet);
        }
    }

    public int runUpdate(@NotNull Connection connection, String sql) throws SQLException {
        int updated;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            updated = statement.getUpdateCount();
        }
        return updated;
    }


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

