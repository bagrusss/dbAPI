package ru.bagrusss.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import ru.bagrusss.helpers.DBHelper;

import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */


@SuppressWarnings("all")
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
    protected static final String SORT = "sort";
    protected static final String FLAT = "flat";
    protected static final String TREE = "tree";
    protected static final String PARENT_TREE = "parent_tree";
    protected static final int HEX_PREFIX = 0x8000;
    protected static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());
    protected static final Gson mGSON = new Gson();
    protected static final DBHelper mHelper = DBHelper.getInstance();


    private JsonObject parseUserWithoutEmail(ResultSet rs, JsonObject result) throws SQLException {
        result.addProperty(ID, rs.getInt(1));
        result.addProperty(ABOUT, rs.getString(ABOUT));
        result.addProperty(NAME, rs.getString(NAME));
        result.addProperty(IS_ANNONIMOUS, rs.getBoolean(IS_ANNONIMOUS));
        result.addProperty(USERNAME, rs.getString(USERNAME));
        return result;
    }

    protected JsonArray getListByEmail(Connection connection, String table, String what, String whereField, String email) throws SQLException {
        JsonArray res = new JsonArray();
        String sql = "SELECT " + what + " FROM " + table +
                " WHERE " + whereField + " = \'" + email + '\'';
        mHelper.runQuery(connection, sql, rs -> {
            while (rs.next()) {
                res.add(rs.getString(1));
            }
        });
        return res;
    }

    protected JsonArray getSubscriptions(Connection connection, String user) throws SQLException {
        JsonArray res = new JsonArray();
        mHelper.runQuery(connection, DBHelper.SUBSCRIPTIONS_QUERY + '\'' + user + '\'', rs -> {
            while (rs.next()) {
                res.add(rs.getInt(1));
            }
        });
        return res;
    }

    @Nullable
    protected JsonObject getUserDetails(Connection connection, String email) throws SQLException {

        /* SELECT * FROM `User` WHERE `email` = ?
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?
           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?
           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?
           */
        JsonObject result = new JsonObject();
        mHelper.runQuery(connection, DBHelper.USER_DETAILS_QUERY + '\'' + email + '\'', rs -> {
            if (rs.next()) {
                parseUserWithoutEmail(rs, result);
                result.addProperty(EMAIL, email);
            }
        });
        JsonArray followers = getListByEmail(connection, DBHelper.TABLE_FOLLOWERS, FOLLOWING_EMAIL, FOLLOWER_EMAIL, email);
        JsonArray following = getListByEmail(connection, DBHelper.TABLE_FOLLOWERS, FOLLOWER_EMAIL, FOLLOWING_EMAIL, email);
        JsonArray subscriptions = getSubscriptions(connection, email);
        result.add(FOLLOWERS, followers);
        result.add(FOLLOWING, following);
        result.add(SUBSCTIPTIOS, subscriptions);
        return result;
    }

    protected JsonArray getUsersDetails(Connection conn, ResultSet rs) throws SQLException {
        JsonArray ja = new JsonArray();
        if (rs.next()) {
            rs.beforeFirst();
            try (PreparedStatement prFollowers = mHelper.getPreparedStatementFollowers(conn);
                 PreparedStatement prFollowing = mHelper.getPreparedStatementFollowing(conn);
                 PreparedStatement prSubscriptions = mHelper.getPreparedStatementSubscript(conn)) {
                while (rs.next()) {
                    String email = rs.getString(EMAIL);
                    JsonObject user =
                            getUserFFS(new PreparedStatement[]{prFollowers, prFollowing, prSubscriptions}, email);
                    parseUserWithoutEmail(rs, user);
                    ja.add(user);
                }
            }
        }
        return ja;
    }

    /**
     * Для списков
     *
     * @param pss   PreparedStaement [] for followers, following, subscriptions, user
     * @param email for user with this email
     * @return JsonObject with addition information
     * @throws SQLException
     */
    protected JsonObject getUserDetails(PreparedStatement[] pss, String email) throws SQLException {
        JsonObject user = getUserFFS(pss, email);
        try (ResultSet rs = pss[3].executeQuery()) {
            if (rs.next()) {
                parseUserWithoutEmail(rs, user);
                user.addProperty(EMAIL, email);
            }
        }
        return user;
    }

    /**
     * Для списков
     *
     * @param pss   PreparedStaement [] for followers, follower, subscriptions
     * @param email for user with this email
     * @return JsonObject with addition information
     * @throws SQLException
     */
    protected JsonObject getUserFFS(PreparedStatement[] pss, String email) throws SQLException {
        JsonObject jo = new JsonObject();
        jo.add(FOLLOWERS, getListByEmail(pss[0], email));
        jo.add(FOLLOWING, getListByEmail(pss[1], email));
        jo.add(SUBSCTIPTIOS, getSubscriptionsByEmail(pss[2], email));
        jo.addProperty(EMAIL, email);
        return jo;
    }

    protected JsonArray getListByEmail(PreparedStatement pr, String email) throws SQLException {
        JsonArray ja = new JsonArray();
        pr.setString(1, email);
        try (ResultSet rs = pr.executeQuery()) {
            while (rs.next()) {
                ja.add(rs.getString(1));
            }
        }
        return ja;
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

    protected long toggleField(Connection connection, String table, long id, String field, boolean value) throws SQLException {
        String sql = "UPDATE " + table + "SET " + field +
                " = " + value + " WHERE id = " + id;
        return mHelper.updateAndGetID(connection, sql);
    }

    protected JsonObject parseThread(ResultSet rs, @Nullable JsonObject result) throws SQLException {
        if (result == null)
            result = new JsonObject();
        result.addProperty(ID, rs.getInt(1));
        result.addProperty(DATE, rs.getString("dt"));
        result.addProperty(LIKES, rs.getInt(LIKES));
        result.addProperty(DISLIKES, rs.getInt(DISLIKES));
        result.addProperty(POINTS, rs.getInt(POINTS));
        result.addProperty(MESSAGE, rs.getString(MESSAGE));
        result.addProperty(TITLE, rs.getString(TITLE));
        result.addProperty(SLUG, rs.getString(SLUG));
        result.addProperty(USER, rs.getString("user_email"));
        result.addProperty(FORUM, rs.getString(FORUM));
        result.addProperty(IS_DELETED, rs.getBoolean(IS_DELETED));
        result.addProperty(IS_CLOSED, rs.getBoolean(IS_CLOSED));
        result.addProperty(POSTS, rs.getLong(POSTS));
        return result;
    }

    protected JsonObject getThreadDetails(Connection connection, long id) throws SQLException {
        JsonObject reslult = new JsonObject();
        mHelper.runQuery(connection, DBHelper.THREAD_DETAILS_QUERY + id, rs -> {
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
        result.addProperty(IS_EDITED, rs.getBoolean(IS_EDITED));
        result.addProperty(IS_HIGHLIGHTED, rs.getBoolean(IS_HIGHLIGHTED));
        result.addProperty(IS_SPAM, rs.getBoolean(IS_SPAM));
        result.addProperty(MESSAGE, rs.getString(MESSAGE));
        result.addProperty(LIKES, rs.getLong(LIKES));
        result.addProperty(DISLIKES, rs.getLong(DISLIKES));
        result.addProperty(FORUM, rs.getString("forum_short_name"));
        result.addProperty(DATE, rs.getString("pd"));
        result.addProperty(USER, rs.getString("user_email"));
        result.addProperty(POINTS, rs.getLong(POINTS));
        long parent = rs.getLong(PARENT);
        result.addProperty(PARENT, parent == 0 ? null : parent);
        result.addProperty(THREAD, rs.getLong("thread_id"));
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    protected JsonObject getPostDetails(Connection connection, long id) throws SQLException {
        String sql = DBHelper.POST_DETAILS_QUERY + id;
        return mHelper.runTypedQuery(connection, sql, rs -> {
            if (rs.next()) {
                return parsePost(rs, null);
            }
            return null;
        });
    }

    @Nullable
    protected JsonObject getForumDetails(PreparedStatement pr, String forum) throws SQLException {
        pr.setString(1, forum);
        try (ResultSet rs = pr.executeQuery()) {
            if (rs.next()) {
                return parseForum(rs);
            }
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected JsonObject getForumDetails(Connection connection, String forum) throws SQLException {
        return mHelper.runTypedQuery(connection, DBHelper.FORUM_DETAILS_QUERY + '\"' + forum + '\"',
                rs -> {
                    if (rs.next()) {
                        return parseForum(rs);
                    }
                    return null;
                });
    }

    protected JsonObject parseForum(ResultSet rs) throws SQLException {
        JsonObject result = new JsonObject();
        result.addProperty(ID, rs.getInt(1));
        result.addProperty(NAME, rs.getString(2));
        result.addProperty(SHORT_NAME, rs.getString(3));
        result.addProperty(USER, rs.getString(4));
        return result;
    }

    @Nullable
    protected JsonObject vote(Connection connection, String table, long id, byte value) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE").append(table).append("SET ");
        if (value == 1)
            sql.append("`likes`=`likes` ");
        else
            sql.append("`dislikes`=`dislikes` ");
        sql.append("+1 WHERE `id`=").append(id);
        mHelper.runUpdate(connection, sql.toString());
        JsonObject result;
        if (table.equals(DBHelper.TABLE_THREAD))
            result = getThreadDetails(connection, id);
        else result = getPostDetails(connection, id);
        return result;
    }
}
