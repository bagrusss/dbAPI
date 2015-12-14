package ru.bagrusss.servlets.forum;

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
 * Created by vladislav
 */

public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String forum = req.getParameter("forum");
        /*
            SELECT * FROM `Forum` WHERE short_name = ?;

         */
        StringBuilder sql = new StringBuilder("SELECT * FROM")
                .append(Helper.TABLE_FORUM)
                .append("WHERE `short_name` = ").append('\"')
                .append(forum)
                .append('\"');
        JsonObject result = new JsonObject();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
                if (rs.next()) {
                    result.addProperty("id", rs.getInt(1));
                    result.addProperty("short_name", forum);
                    result.addProperty("name", rs.getString("name"));
                    result.addProperty("user", rs.getString("user_email"));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);
    }
}
