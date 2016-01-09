package ru.bagrusss.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Helper;

import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */


@SuppressWarnings("ConstantNamingConvention")
public class BaseServlet extends HttpServlet {

    public static final String DEFAULT_ENCODING = "UTF-8";
    protected static final String BASE_URL = "/db/api";
    protected final Gson mGSON = new Gson();
    //user
    protected static final String USERNAME = "username";
    protected static final String USER = "user";
    protected static final String EMAIL = "email";
    protected static final String ABOUT = "about";
    protected static final String NAME = "name";
    protected static final String ID = "id";
    protected static final String IS_ANNONIMOUS = "isAnonymous";
    protected static final String FOLLOWING = "following";
    protected static final String FOLLOWERS = "followers";
    protected static final String SUBSCTIPTIOS = "subscriptions";
    //followers
    protected static final String FOLLOWER_EMAIL = "follower_email";
    protected static final String FOLLOWING_EMAIL = "following_email";
    protected static Logger logger = Logger.getLogger(BaseServlet.class.getName());
    protected final Helper mHelper = DBHelper.getInstance();

    @Nullable
    protected JsonObject getUserDetails(Connection connection, String email, boolean isFull) throws SQLException {

        /* SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)

           */
        String sql = "SELECT * FROM " + Helper.TABLE_USER +
                " WHERE " + "`email` = \'" + email + '\'';
        JsonObject result = new JsonObject();
        final boolean[] success = {false};
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                result.addProperty(ABOUT, rs.getString(ABOUT));
                result.addProperty(NAME, rs.getString(NAME));
                result.addProperty(ID, rs.getInt(ID));
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

    protected long toggleField(Connection connection, String table, long id, String field, boolean value) throws SQLException {
        String sql = "UPDATE " +
                table +
                "SET " + field +
                " = " + value +
                " WHERE id = " + id;
        return mHelper.updateAndGetID(connection, sql);
    }

    protected JsonObject getThreadDetails(Connection connection, long id) throws SQLException {
        String sql = "SELECT *, likes-CAST(dislikes AS SIGNED) points, " +
                "DATE_FORMAT(`date`, '%Y-%m-%d %H:%i:%s') dt " +
                "FROM" +
                Helper.TABLE_THREAD +
                "WHERE `id` = " + id;
        JsonObject reslult = new JsonObject();
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                parseThread(rs, reslult);
            }
        });
        return reslult;
    }

    protected JsonObject parsePost(ResultSet rs, @Nullable JsonObject result) throws SQLException {
        if (result == null)
            result = new JsonObject();
        result.addProperty("id", rs.getLong(1));
        result.addProperty("isApproved", rs.getBoolean("isApproved"));
        result.addProperty("isDeleted", rs.getBoolean("isDeleted"));
        result.addProperty("isEdited", rs.getBoolean("isEdited"));
        result.addProperty("isHighlighted", rs.getBoolean("isHighlighted"));
        result.addProperty("isSpam", rs.getBoolean("isSpam"));
        result.addProperty("message", rs.getString("message"));
        result.addProperty("forum", rs.getString("forum_short_name"));
        result.addProperty("date", rs.getString("pd"));
        result.addProperty("user", rs.getString("user_email"));
        result.addProperty("dislikes", rs.getLong("dislikes"));
        result.addProperty("likes", rs.getLong("likes"));
        result.addProperty("points", rs.getLong("points"));
        long parent = rs.getLong("parent");
        result.addProperty("parent", parent == 0 ? null : parent);
        result.addProperty("thread", rs.getLong("thread_id"));
        return result;
    }

    @Nullable
    protected JsonObject getPostDetails(Connection connection, long id) throws SQLException {
        String sql = "SELECT *, likes-CAST(dislikes AS SIGNED) points, " +
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
        String sql = "SELECT * FROM" +
                Helper.TABLE_FORUM +
                "WHERE `short_name` = " + '\"' +
                forum +
                '\"';
        JsonObject result = new JsonObject();
        final boolean[] isFound = {false};
        mHelper.runQuery(connection, sql, rs -> {
            if (rs.next()) {
                result.addProperty("id", rs.getInt(1));
                result.addProperty("short_name", forum);
                result.addProperty("name", rs.getString("name"));
                result.addProperty("user", rs.getString("user_email"));
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
        switch (value) {
            case 1:
                sql.append("`likes`=`likes` ");
                break;
            case -1:
                sql.append("`dislikes`=`dislikes` ");
                break;
            default:
                return null;
        }
        sql.append("+1 WHERE `id`=").append(id);
        mHelper.runUpdate(connection, sql.toString());
        JsonObject result;
        if (table.equals(Helper.TABLE_THREAD))
            result = getThreadDetails(connection, id);
        else result = getPostDetails(connection, id);
        return result;
    }
}
