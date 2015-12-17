package ru.bagrusss.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Helper;

import javax.servlet.http.HttpServlet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by vladislav
 */

public class BaseServlet extends HttpServlet {

    protected static final String BASE_URL = "/db/api";
    protected final Helper mHelper = DBHelper.getInstance();
    protected final Gson mGson = new Gson();
    public static final String DEFAULT_ENCODING = "UTF-8";

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

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Nullable
    protected JsonObject getUserDetails(String email, boolean isFull) throws SQLException {

        /* SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)

           */
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(Helper.TABLE_USER)
                .append(" WHERE ").append("`email` = \'");
        sqlBuilder.append(email).append('\'');
        JsonObject result = new JsonObject();
        final boolean[] success = {false};

        mHelper.runQuery(mHelper.getConnection(), sqlBuilder.toString(), rs -> {
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
                followers = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWING_EMAIL, FOLLOWER_EMAIL, email);
                following = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWER_EMAIL, FOLLOWING_EMAIL, email);
                subscriptions = getSubscriptions(email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            result.add(FOLLOWERS, followers);
            result.add(FOLLOWING, following);
            result.add(SUBSCTIPTIOS, subscriptions);
        }
        return result;
    }

    //for /user/details
    protected JsonArray getListByEmail(String table, String what, String whereField, String email) throws SQLException {
        JsonArray res = new JsonArray();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ").append(what)
                .append(" FROM ").append(table)
                .append(" WHERE ").append(whereField).append(" = \'")
                .append(email).append('\'');
        mHelper.runQuery(mHelper.getConnection(), sqlBuilder.toString(), rs -> {
            while (rs.next()) {
                res.add(rs.getString(1));
            }
        });
        return res;
    }

    protected JsonArray getSubscriptions(String user) throws SQLException {
        JsonArray res = new JsonArray();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ").append("`thread_id`")
                .append(" FROM ").append(Helper.TABLE_SUBSCRIPTIONS)
                .append(" WHERE ").append("`user_email`").append(" = ")
                .append('\'').append(user).append('\'');
        mHelper.runQuery(mHelper.getConnection(), sqlBuilder.toString(), rs -> {
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

    protected long toggleField(String table, long id, String field, boolean value) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(table)
                .append("SET ").append(field)
                .append(" = ").append(value)
                .append(" WHERE id = ").append(id);
        return mHelper.updateAndGetID(mHelper.getConnection(), sql.toString());
    }

    protected JsonObject getThreadDetails(long id) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(`date`, '%Y-%m-%d %H:%i:%s') dt ")
                .append("FROM")
                .append(Helper.TABLE_THREAD)
                .append("WHERE `id` = ").append(id);
        JsonObject reslult = new JsonObject();
        mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
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
    protected JsonObject getPostDetails(long id) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd FROM")
                .append(Helper.TABLE_POST)
                .append("WHERE `id`=")
                .append(id);
        JsonObject result = new JsonObject();
        final boolean[] isFound = {false};
        mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
            if (rs.next()) {
                parsePost(rs, result);
                isFound[0] = true;
            }
        });
        return isFound[0] ? result : null;
    }

    @Nullable
    protected JsonObject getForumDetails(String forum) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM")
                .append(Helper.TABLE_FORUM)
                .append("WHERE `short_name` = ").append('\"')
                .append(forum)
                .append('\"');
        JsonObject result = new JsonObject();
        final boolean[] isFound = {false};
        mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
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
    protected JsonObject vote(String table, long id, byte value) throws SQLException {
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
        mHelper.runUpdate(mHelper.getConnection(), sql.toString());
        JsonObject result;
        if (table.equals(Helper.TABLE_THREAD))
            result = getThreadDetails(id);
        else result = getPostDetails(id);
        return result;
    }
}
