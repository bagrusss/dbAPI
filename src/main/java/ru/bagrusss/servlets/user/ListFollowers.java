package ru.bagrusss.servlets.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
public class ListFollowers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listFollowers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           или
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? AND ?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? ORDER BY `name` ? LIMIT ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?;

           select u.*, f.following_email from User u Join Followers f
           on f.follower_email=u.email where u.email="example2@mail.ru";
         */
        String parameter = req.getParameter("user");
        StringBuilder sql = new StringBuilder("SELECT u.email FROM")
                .append(Helper.TABLE_USER).append("u ")
                .append("INNER JOIN")
                .append(Helper.TABLE_FOLLOWERS).append("f ")
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
