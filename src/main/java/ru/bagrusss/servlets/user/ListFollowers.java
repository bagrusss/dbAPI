package ru.bagrusss.servlets.user;

import com.google.gson.JsonArray;
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
 * Created by vladislav on 20.10.15.
 */
public class ListFollowers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listFollowers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT STRAIGHT_JOIN u.email FROM User u FORCE INDEX(Name_id)
            JOIN Post p FORCE INDEX (ForumShortName_UserEmail)
            ON u.email=p.user_email WHERE p.forum_short_name = ""
            AND u.id>=10 GROUP BY u.`name` ORDER BY u.`name`
         */
        String parameter = req.getParameter("user");
        StringBuilder sql = new StringBuilder("SELECT STRAIGHT_JOIN ")
                .append("u.email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX (Name_id) ")
                .append("INNER JOIN")
                .append(Helper.TABLE_FOLLOWERS).append("f FORCE INDEX (following_follower) ")
                .append("ON f.following_email=u.email ")
                .append("WHERE f.follower_email=").append('\'')
                .append(parameter).append("\' ");
        parameter = req.getParameter("since_id");
        if (parameter != null)
            sql.append(" AND u.id >= ").append(parameter);
        parameter = req.getParameter("order");
        if (parameter != null)
            sql.append(" ORDER BY u.name ").append(parameter);
        parameter = req.getParameter("limit");
        if (parameter != null)
            sql.append(" LIMIT ").append(parameter);
        JsonArray followers = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    followers.add(getUserDetails(connection, rs.getString(1), true));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), followers);
    }
}
