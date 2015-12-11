package ru.bagrusss.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Helper;

import javax.servlet.http.HttpServlet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // subsctiptios
    protected static final String USER_EMAIL = "user_email";
    protected static final String THREAD_ID = "thread_id";

    @Nullable
    protected JsonObject getUserDetails(String email, boolean is_full) throws SQLException {

        /* SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)

           */

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(Helper.TABLE_USER)
                .append(" WHERE ").append("`email` = ?;");
        List<Object> sqlParams = new ArrayList<>(1);
        sqlParams.add(email);
        JsonObject result = new JsonObject();
        final boolean[] success = {false};

        mHelper.runPreparedQuery(mHelper.getConnection(), sqlBuilder.toString(), sqlParams, rs -> {
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
        if (is_full) {

            JsonArray followers = null;
            JsonArray following = null;
            JsonArray subscriptions = null;
            try {
                followers = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWING_EMAIL, FOLLOWER_EMAIL, sqlParams);
                following = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWER_EMAIL, FOLLOWING_EMAIL, sqlParams);
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
    protected JsonArray getListByEmail(String table, String what, String whereField, List<?> params) throws SQLException {
        JsonArray res = new JsonArray();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ").append(what)
                .append(" FROM ").append(table)
                .append(" WHERE ").append(whereField).append(" = ?");
        mHelper.runPreparedQuery(mHelper.getConnection(), sqlBuilder.toString(), params, rs -> {
            while (rs.next()) {
                res.add(rs.getString(1));
            }
        });
        return res;
    }

    protected JsonArray getSubscriptions(String user) throws SQLException {
        JsonArray res = new JsonArray();
        List<Object> params = new ArrayList<>(1);
        params.add(user);
        StringBuilder sqlBuilder = new StringBuilder("SELECT ").append("`thread_id`")
                .append(" FROM ").append(Helper.TABLE_SUBSCRIPTIONS)
                .append(" WHERE ").append("`user_email`").append(" = ?");
        mHelper.runPreparedQuery(mHelper.getConnection(), sqlBuilder.toString(), params, rs -> {
            while (rs.next()) {
                //res.add(rs.getObject(1).toString());
                res.add(rs.getInt(1));
            }
        });
        return res;
    }

    protected JsonObject getForumDetails(long id, @Nullable String[] related) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT t.*, t.likes-t.dislikes points, ")
                .append("DATE_FORMAT(t.date, '%Y-%m-%d %H:%i:%s') ")
                .append("dt, COUNT(p.id) posts FROM")
                .append(Helper.TABLE_THREAD)
                .append("t, ").append(Helper.TABLE_POST)
                .append("p WHERE t.`id` = ").append(id)
                .append(" AND p.isDeleted = False");
        JsonObject reslult = new JsonObject();
        mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
            if (rs.next()) {
                reslult.addProperty("id", id);
                reslult.addProperty("date", rs.getString("dt"));
                reslult.addProperty("likes", rs.getInt("likes"));
                reslult.addProperty("dislikes", rs.getInt("dislikes"));
                reslult.addProperty("points", rs.getInt("points"));
                reslult.addProperty("message", rs.getString("message"));
                reslult.addProperty("title", rs.getString("title"));
                reslult.addProperty("slug", rs.getString("slug"));
                reslult.addProperty("user", rs.getString("user_email"));
                reslult.addProperty("forum", rs.getString("forum"));
                reslult.addProperty("isDeleted", rs.getBoolean("isDeleted"));
                reslult.addProperty("isClosed", rs.getBoolean("isClosed"));
                reslult.addProperty("posts", rs.getLong("posts"));
            }
        });
        return reslult;
    }

    protected boolean toggleThreadField(long id, String field, boolean value) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(Helper.TABLE_THREAD)
                .append("SET ").append(field)
                .append(" = ").append(value)
                .append(" WHERE id = ").append(id);
        return mHelper.runUpdate(mHelper.getConnection(), sql.toString()) > 0;
    }
}
