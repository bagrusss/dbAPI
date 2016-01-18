package ru.bagrusss.servlets.forum;

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
 * Created by vladislav
 */
public class ListUsers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listUsers/";

    @SuppressWarnings("Duplicates")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String param = req.getParameter(FORUM);
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(DBHelper.TABLE_USER).append("u WHERE `email` IN (")
                .append("SELECT DISTINCT user_email FROM")
                .append(DBHelper.TABLE_POST).append("FORCE INDEX (ForumShortName_UserEmail) ")
                .append("WHERE forum_short_name=\'").append(param).append("\') ");
        param = req.getParameter(SINCE_ID);
        if (param != null) {
            sql.append(" AND u.`id` >= ").append(param);
        }
        param = req.getParameter(ORDER);
        if (param != null) {
            sql.append(" ORDER BY u.`name` ").append(param);
        }
        param = req.getParameter(LIMIT);
        if (param != null) {
            sql.append(" LIMIT ").append(param);
        }
        JsonArray users;
        try (Connection connection = mHelper.getConnection()) {
            users = mHelper.runTypedQuery(connection, sql.toString(),
                    rs -> getUsersDetails(connection, rs));
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), users);
    }
}
