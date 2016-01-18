package ru.bagrusss.servlets.user;

import com.google.gson.JsonArray;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Errors;
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
    @SuppressWarnings("Duplicates")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String parameter = req.getParameter(USER);
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(DBHelper.TABLE_USER).append("u INNER JOIN (")
                .append("SELECT DISTINCT following_email FROM ")
                .append(DBHelper.TABLE_FOLLOWERS).append("FORCE INDEX (PRIMARY) ")
                .append("WHERE follower_email=\'").append(parameter).append("\') f ")
                .append("ON f.following_email=u.email ");
        parameter = req.getParameter(SINCE_ID);
        if (parameter != null)
            sql.append(" AND u.id >= ").append(parameter);
        parameter = req.getParameter(ORDER);
        if (parameter != null)
            sql.append(" ORDER BY u.name ").append(parameter);
        parameter = req.getParameter(LIMIT);
        if (parameter != null)
            sql.append(" LIMIT ").append(parameter);
        JsonArray followers;
        try (Connection connection = mHelper.getConnection()) {
            followers = mHelper.runTypedQuery(connection, sql.toString(),
                    rs -> getUsersDetails(connection, rs));
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), followers);
    }
}
