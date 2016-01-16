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
        String parameter = req.getParameter(USER);
        StringBuilder sql = new StringBuilder("SELECT STRAIGHT_JOIN ")
                .append("f.following_email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX (Name_email_id) ") //TODO!!!
                .append("INNER JOIN")
                .append(Helper.TABLE_FOLLOWERS).append("f FORCE INDEX (primary) ")
                .append("ON f.following_email=u.email ")
                .append("WHERE f.follower_email=").append('\'')
                .append(parameter).append("\' ");
        parameter = req.getParameter(SINCE_ID);
        if (parameter != null)
            sql.append(" AND u.id >= ").append(parameter);
        parameter = req.getParameter(ORDER);
        sql.append(" ORDER BY u.name ");
        if (parameter != null) //TODO сделать принудительную сортировку для использования индекса
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
