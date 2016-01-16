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

public class ListFollowing extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listFollowing/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

         */
        String parameter = req.getParameter(USER);
        StringBuilder sql = new StringBuilder("SELECT STRAIGHT_JOIN f.follower_email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX (Name_email_id) ") //TODO!!!
                .append("INNER JOIN")
                .append(Helper.TABLE_FOLLOWERS).append("f FORCE INDEX (following_follower) ")
                .append("ON f.follower_email=u.email ")
                .append("WHERE f.following_email=").append('\'')
                .append(parameter).append("\' ");
        parameter = req.getParameter(SINCE_ID);
        if (parameter != null)
            sql.append(" AND u.id >= ").append(parameter);
        parameter = req.getParameter(ORDER);
        sql.append(" ORDER BY u.name ");
        if (parameter != null)
            sql.append(parameter);
        parameter = req.getParameter(LIMIT);
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
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), followers);
    }

}
