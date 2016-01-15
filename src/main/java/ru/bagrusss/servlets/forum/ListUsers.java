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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

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
            AND u.id>=10 ORDER BY u.`name` LIMIT 10

            SELECT user_email FROM Post WHERE forum_short_name="aaa";
            SELECT * FROM User WHERE email IN ();
         */
        StringBuilder sql = new StringBuilder("SELECT user_email FROM")
                .append(Helper.TABLE_POST)
                .append("FORCE INDEX (ForumShortName_UserEmail) ")
                .append("WHERE forum_short_name=\'").append(param).append('\'');
        JsonArray usrs = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            String users = mHelper.runTypedQuery(connection, sql.toString(), rs -> {
                StringBuilder builder = new StringBuilder();
                while (rs.next()) {
                    builder.append(rs.getString(1)).append("\',\'");
                }
                if (builder.length() > 0) {
                    builder.replace(builder.length() - 3, builder.length(), "");
                }
                return builder.toString();
            });
            if (users.isEmpty()) {
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
                String followers = "SELECT following_email FROM Followers WHERE follower_email=?";
                String following = "SELECT follower_email FROM Followers WHERE following_email=?";
                String subscriptions = "SELECT thread_id FROM Subscriptions WHERE user_email=?";
                try (PreparedStatement preparedFollowers = connection.prepareStatement(followers);
                     PreparedStatement preparedFollowong = connection.prepareStatement(following);
                     PreparedStatement preparedSubscriptions = connection.prepareStatement(subscriptions)) {
                    while (rs.next()) {
                        JsonObject usr = new JsonObject();
                        String email = rs.getString(EMAIL);
                        usr.add(FOLLOWING, getListByEmail(preparedFollowers, email));
                        usr.add(FOLLOWERS, getListByEmail(preparedFollowong, email));
                        usr.add(SUBSCTIPTIOS, getSubscriptionsByEmail(preparedSubscriptions, email));
                        usr.addProperty(EMAIL, email);
                        usrs.add(parseUserWithoutEmail(rs, usr));
                    }
                }
            });
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, sql.toString());
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), usrs);
    }
}
