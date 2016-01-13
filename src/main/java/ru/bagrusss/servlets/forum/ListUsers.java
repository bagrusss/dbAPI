package ru.bagrusss.servlets.forum;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by vladislav
 */
public class ListUsers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listUsers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String param = req.getParameter("forum");
        /*
            SELECT STRAIGHT_JOIN u.email FROM User u FORCE INDEX(Name_id)
            JOIN Post p FORCE INDEX (ForumShortName_UserEmail)
            ON u.email=p.user_email WHERE p.forum_short_name = ""
            AND u.id>=10 GROUP BY u.`name` ORDER BY u.`name` LIMIT 10

            SELECT user_email FROM Post WHERE forum_short_name="aaa";
            SELECT * FROM User WHERE email IN ();
         */
        StringBuilder sql = new StringBuilder();
/*        sql.append("SELECT STRAIGHT_JOIN u.email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX(Name_id) ")
                .append("JOIN ").append(Helper.TABLE_POST)
                .append("p FORCE INDEX (ForumShortName_UserEmail) ")
                .append("ON u.email=p.user_email ")
                .append("WHERE p.forum_short_name = '")
                .append(param).append("\' ");*/
        sql.append("SELECT DISTINCT user_email FROM").append(Helper.TABLE_POST)
                .append("FORCE INDEX (ForumShortName_UserEmail) ")
                .append("WHERE forum_short_name=\'").append(param).append('\'');
        JsonArray usrs = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            String users = mHelper.runTypedQuery(connection, sql.toString(), rs -> {
                if (rs.next()) {
                    StringBuilder builder = new StringBuilder();
                    rs.beforeFirst();
                    while (rs.next()) {
                        builder.append(rs.getString(1)).append("\',\'");
                    }
                    return builder.replace(builder.length() - 3, builder.length(), "").toString();
                }
                return null;
            });
            if (users == null) {
                Errors.correct(resp.getWriter(), usrs);
                return;
            }
            sql.setLength(0);
            sql.append("SELECT * FROM").append(Helper.TABLE_USER)
                    .append("FORCE INDEX (Name_email_id)")
                    .append(" WHERE email IN (\'").append(users).append("\')");
            param = req.getParameter("since_id");
            if (param != null) {
                sql.append(" AND id >= ").append(param);
            }
            sql.append(" ORDER BY `name` ");
            param = req.getParameter("order");
            if (param != null) {
                sql.append(param);
            }
            param = req.getParameter("limit");
            if (param != null) {
                sql.append(" LIMIT ").append(param);
            }
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject usr = new JsonObject();
                    String email = rs.getString(EMAIL);
                    getSubscriptions(connection, email);
                    usr.add(FOLLOWING, getListByEmail(connection, Helper.TABLE_FOLLOWERS,
                            FOLLOWING_EMAIL, FOLLOWER_EMAIL, email));
                    usr.add(FOLLOWERS, getListByEmail(connection, Helper.TABLE_FOLLOWERS,
                            FOLLOWER_EMAIL, FOLLOWING_EMAIL, email));
                    usr.add(SUBSCTIPTIOS, getSubscriptions(connection, email));
                    usr.addProperty(EMAIL, email);
                    usrs.add(parseUserWithoutEmail(rs, usr));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), usrs);
    }
}
