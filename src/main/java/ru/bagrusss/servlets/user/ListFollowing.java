package ru.bagrusss.servlets.user;

import com.google.gson.JsonArray;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav on 20.10.15.
 */

public class ListFollowing extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listFollowing/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           или
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? AND ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? ORDER BY `name` ? LIMIT ?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?;
         */
        String parameter = req.getParameter("user");
        StringBuilder sql = new StringBuilder("SELECT STRAIGHT_JOIN u.email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX (Name_id) ")
                .append("INNER JOIN")
                .append(Helper.TABLE_FOLLOWERS).append("f FORCE INDEX (primary) ")
                .append("ON f.follower_email=u.email ")
                .append("WHERE f.following_email=").append('\'')
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
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
                while (rs.next()) {
                    followers.add(getUserDetails(rs.getString("email"), true));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), followers);
    }

}
