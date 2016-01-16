package ru.bagrusss.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Helper;

import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */


@SuppressWarnings({"ConstantNamingConvention", "SqlNoDataSourceInspection", "SqlResolve", "unused"})
public class BaseServlet extends HttpServlet {

    public static final String DEFAULT_ENCODING = "UTF-8";
    protected static final String BASE_URL = "/db/api";

    protected static final String SINCE = "since";
    protected static final String ORDER = "order";
    protected static final String USER = "user";
    protected static final String LIMIT = "limit";
    protected static final String RELATED = "related";
    protected static final String SINCE_ID = "since_id";

    protected static final String USERNAME = "username";
    protected static final String EMAIL = "email";
    protected static final String ABOUT = "about";
    protected static final String NAME = "name";
    protected static final String ID = "id";
    protected static final String THREAD = "thread";
    protected static final String FORUM = "forum";
    protected static final String SHORT_NAME = "short_name";
    protected static final String POST = "post";
    protected static final String VOTE = "vote";
    protected static final String TITLE = "title";
    protected static final String SLUG = "slug";
    protected static final String PARENT = "parent";
    protected static final String DATE = "date";
    protected static final String MESSAGE = "message";
    protected static final String DISLIKES = "dislikes";
    protected static final String LIKES = "likes";
    protected static final String POINTS = "points";
    protected static final String POSTS = "posts";
    protected static final String IS_DELETED = "isDeleted";
    protected static final String IS_SPAM = "isSpam";
    protected static final String IS_EDITED = "isEdited";
    protected static final String IS_HIGHLIGHTED = "isHighlighted";
    protected static final String IS_APPROVED = "isApproved";
    protected static final String IS_CLOSED = "isClosed";
    protected static final String IS_ANNONIMOUS = "isAnonymous";
    protected static final String FOLLOWING = "following";
    protected static final String FOLLOWERS = "followers";
    protected static final String SUBSCTIPTIOS = "subscriptions";
    protected static final String FOLLOWEE = "followee";
    protected static final String FOLLOWER = "follower";

    protected static final String FOLLOWER_EMAIL = "follower_email";
    protected static final String FOLLOWING_EMAIL = "following_email";
    protected static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());
    protected static final Gson mGSON = new Gson();
    protected static final Helper mHelper = DBHelper.getInstance();

    protected static final String followersQuery = "SELECT following_email FROM Followers WHERE follower_email=?";
    protected static final String followingQuery = "SELECT follower_email FROM Followers WHERE following_email=?";
    protected static final String subscriptionsQuery = "SELECT thread_id FROM Subscriptions WHERE user_email=?";

    protected JsonObject parseUserWithoutEmail(ResultSet rs, JsonObject result) throws SQLException {
        result.addProperty(ID, rs.getInt(1));
        result.addProperty(ABOUT, rs.getString(ABOUT));
        result.addProperty(NAME, rs.getString(NAME));
        result.addProperty(IS_ANNONIMOUS, rs.getBoolean(IS_ANNONIMOUS));
        result.addProperty(USERNAME, rs.getString(USERNAME));
        return result;
    }

    @Nullable
    protected JsonObject getUserDetails(Connection connection, String email, boolean isFull) throws SQLException {

        /* SELECT * FROM `User` WHERE `email` = ?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;
           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)
           */
        String sql = "SELECT id, username, name, about, email, isAnonymous FROM " + Helper.TABLE_USER +
                " WHERE " + "`email` = \'" + email + '\'';
        JsonObject result = new JsonObject();
        final boolean[] success = {false};
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                result.addProperty(ID, rs.getInt(1));
                result.addProperty(ABOUT, rs.getString(ABOUT));
                result.addProperty(NAME, rs.getString(NAME));
                result.addProperty(EMAIL, rs.getString(EMAIL));
                result.addProperty(IS_ANNONIMOUS, rs.getBoolean(IS_ANNONIMOUS));
                result.addProperty(USERNAME, rs.getString(USERNAME));
                success[0] = true;
            }
        });
        if (!success[0]) {
            return null;
        }
        if (isFull) {
            JsonArray followers = null;
            JsonArray following = null;
            JsonArray subscriptions = null;
            try {
                followers = getListByEmail(connection, Helper.TABLE_FOLLOWERS, FOLLOWING_EMAIL, FOLLOWER_EMAIL, email);
                following = getListByEmail(connection, Helper.TABLE_FOLLOWERS, FOLLOWER_EMAIL, FOLLOWING_EMAIL, email);
                subscriptions = getSubscriptions(connection, email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            result.add(FOLLOWERS, followers);
            result.add(FOLLOWING, following);
            result.add(SUBSCTIPTIOS, subscriptions);
        }
        return result;
    }

    protected JsonArray getListByEmail(Connection connection, String table, String what, String whereField, String email) throws SQLException {
        JsonArray res = new JsonArray();
        String sql = "SELECT " + what +
                " FROM " + table +
                " WHERE " + whereField + " = \'" +
                email + '\'';
        mHelper.runQuery(connection, sql, rs -> {
            while (rs.next()) {
                res.add(rs.getString(1));
            }
        });
        return res;
    }

    protected JsonArray getListByEmail(PreparedStatement pr, String email) throws SQLException {
        JsonArray ja = new JsonArray();
        pr.setString(1, email);
        try (ResultSet followers = pr.executeQuery()) {
            while (followers.next()) {
                ja.add(followers.getString(1));
            }
        }
        return ja;
    }

    protected void prepareUsers(Connection connection, ResultSet rs, JsonArray ja) throws SQLException{
        if (rs.next()) {
            rs.beforeFirst();
            try (PreparedStatement preparedFollowers = connection.prepareStatement(followersQuery);
                 PreparedStatement preparedFollowong = connection.prepareStatement(followingQuery);
                 PreparedStatement preparedSubscriptions = connection.prepareStatement(subscriptionsQuery)) {
                while (rs.next()) {
                    JsonObject user = new JsonObject();
                    String email = rs.getString(EMAIL);
                    user.add(FOLLOWING, getListByEmail(preparedFollowers, email));
                    user.add(FOLLOWERS, getListByEmail(preparedFollowong, email));
                    user.add(SUBSCTIPTIOS, getSubscriptionsByEmail(preparedSubscriptions, email));
                    user.addProperty(EMAIL, email);
                    ja.add(parseUserWithoutEmail(rs, user));
                }
            }
        }
    }

    protected JsonArray getSubscriptionsByEmail(PreparedStatement pr, String email) throws SQLException {
        JsonArray ja = new JsonArray();
        pr.setString(1, email);
        try (ResultSet followers = pr.executeQuery()) {
            while (followers.next()) {
                ja.add(followers.getLong(1));
            }
        }
        return ja;
    }

    protected JsonArray getSubscriptions(Connection connection, String user) throws SQLException {
        JsonArray res = new JsonArray();
        String sql = "SELECT " + "`thread_id`" +
                " FROM " + Helper.TABLE_SUBSCRIPTIONS +
                " WHERE " + "`user_email`" + " = " +
                '\'' + user + '\'';
        mHelper.runQuery(connection, sql, rs -> {
            while (rs.next()) {
                res.add(rs.getInt(1));
            }
        });
        return res;
    }


    protected long toggleField(Connection connection, String table, long id, String field, boolean value) throws SQLException {
        String sql = "UPDATE " +
                table +
                "SET " + field +
                " = " + value +
                " WHERE id = " + id;
        return mHelper.updateAndGetID(connection, sql);
    }

    /**
     * @param rs ResultSet
     * @return return item with id from current in ResultSet
     * @throws SQLException
     */
    protected JsonObject parseThread(ResultSet rs, @Nullable JsonObject result) throws SQLException {
        if (result == null)
            result = new JsonObject();
        result.addProperty("id", rs.getInt(1));
        result.addProperty("date", rs.getString("dt"));
        result.addProperty("likes", rs.getInt("likes"));
        result.addProperty("dislikes", rs.getInt("dislikes"));
        result.addProperty("points", rs.getInt("points"));
        result.addProperty("message", rs.getString("message"));
        result.addProperty("title", rs.getString("title"));
        result.addProperty("slug", rs.getString("slug"));
        result.addProperty("user", rs.getString("user_email"));
        result.addProperty("forum", rs.getString("forum"));
        result.addProperty("isDeleted", rs.getBoolean("isDeleted"));
        result.addProperty("isClosed", rs.getBoolean("isClosed"));
        result.addProperty("posts", rs.getLong("posts"));
        return result;
    }

    protected JsonObject getThreadDetails(Connection connection, long id) throws SQLException {
        String sql = "SELECT id, likes, dislikes, message, title, slug, user_email, forum, " +
                "isDeleted, isClosed, posts, likes-CAST(dislikes AS SIGNED) points, " +
                "DATE_FORMAT(`date`, '%Y-%m-%d %H:%i:%s') dt " +
                "FROM" + Helper.TABLE_THREAD + "WHERE `id` = " + id;
        JsonObject reslult = new JsonObject();
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                parseThread(rs, reslult);
            }
        });
        return reslult;
    }

    protected JsonObject getThreadDetails(PreparedStatement pr, long id) throws SQLException {
        JsonObject reslult = new JsonObject();
        pr.setLong(1, id);
        try (ResultSet rs = pr.executeQuery()) {
            if (rs.next()) {
                return parseThread(rs, reslult);
            }
        }
        return reslult;
    }

    protected JsonObject parsePost(ResultSet rs, @Nullable JsonObject result) throws SQLException {
        if (result == null)
            result = new JsonObject();
        result.addProperty(ID, rs.getLong(1));
        result.addProperty(IS_APPROVED, rs.getBoolean(IS_APPROVED));
        result.addProperty(IS_DELETED, rs.getBoolean(IS_DELETED));
        result.addProperty("isEdited", rs.getBoolean("isEdited"));
        result.addProperty("isHighlighted", rs.getBoolean("isHighlighted"));
        result.addProperty("isSpam", rs.getBoolean("isSpam"));
        result.addProperty("message", rs.getString("message"));
        result.addProperty("likes", rs.getLong("likes"));
        result.addProperty("dislikes", rs.getLong("dislikes"));
        result.addProperty("forum", rs.getString("forum_short_name"));
        result.addProperty("date", rs.getString("pd"));
        result.addProperty("user", rs.getString("user_email"));
        result.addProperty("points", rs.getLong("points"));
        long parent = rs.getLong("parent");
        result.addProperty("parent", parent == 0 ? null : parent);
        result.addProperty("thread", rs.getLong("thread_id"));
        return result;
    }

    @Nullable
    protected JsonObject getPostDetails(Connection connection, long id) throws SQLException {
        String sql = "SELECT id, isApproved, isDeleted, isEdited, isHighlighted, isSpam, " +
                "message, likes, dislikes, thread_id, user_email, forum_short_name, parent, " +
                "likes-CAST(dislikes AS SIGNED) points, " +
                "DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd FROM" +
                Helper.TABLE_POST +
                "WHERE `id`=" +
                id;
        JsonObject result = new JsonObject();
        final boolean[] isFound = {false};
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                parsePost(rs, result);
                isFound[0] = true;
            }
        });
        return isFound[0] ? result : null;
    }

    @Nullable
    protected JsonObject getForumDetails(Connection connection, String forum) throws SQLException {
        String sql = "SELECT `id`, `name`, `short_name`, user_email FROM" +
                Helper.TABLE_FORUM +
                "WHERE `short_name` = " + '\"' +
                forum +
                '\"';
        JsonObject result = new JsonObject();
        final boolean[] isFound = {false};
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                result.addProperty("id", rs.getInt(1));
                result.addProperty("name", rs.getString(2));
                result.addProperty("short_name", rs.getString(3));
                result.addProperty("user", rs.getString(4));
                isFound[0] = true;
            }
        });
        return isFound[0] ? result : null;
    }

    @Nullable
    protected JsonObject vote(Connection connection, String table, long id, byte value) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE")
                .append(table)
                .append("SET ");
        if (value == 1)
            sql.append("`likes`=`likes` ");
        else
            sql.append("`dislikes`=`dislikes` ");
        sql.append("+1 WHERE `id`=").append(id);
        mHelper.runUpdate(connection, sql.toString());
        JsonObject result;
        if (table.equals(Helper.TABLE_THREAD))
            result = getThreadDetails(connection, id);
        else result = getPostDetails(connection, id);
        return result;
    }
}
