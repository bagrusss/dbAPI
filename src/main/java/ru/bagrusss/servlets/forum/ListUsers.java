package ru.bagrusss.servlets.forum;

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
 * Created by vladislav
 */
public class ListUsers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listUsers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        //по-хорошему параметры нужно проверять regexp'ами
        String param = req.getParameter("forum");
        /*
            SELECT * FROM `User` u JOIN `Forum` f
            ON u.email=f.user_email
            WHERE f.short_name = ? AND u.id >= ? ORDER BY u.name LIMIT ?;
         */
        StringBuilder sql = new StringBuilder("SELECT DISTINCT u.email FROM")
                .append(Helper.TABLE_POST).append("p ")
                .append("JOIN ").append(Helper.TABLE_USER).append("u ")
                .append("ON u.email=p.user_email ")
                .append("WHERE p.forum_short_name = '")
                .append(param).append("\' ");
        param = req.getParameter("since_id");
        if (param != null) {
            sql.append(" AND u.id >= ").append(param);
        }
        param = req.getParameter("order");
        if (param != null) {
            sql.append(" ORDER BY u.`name` ").append(param);
        }
        param = req.getParameter("limit");
        if (param != null) {
            sql.append(" LIMIT ").append(param);
        }
        JsonArray users = new JsonArray();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
                while (rs.next()) {
                    String user = rs.getString(1);
                    if (user != null)
                        users.add(getUserDetails(user, true));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), users);
    }
}
