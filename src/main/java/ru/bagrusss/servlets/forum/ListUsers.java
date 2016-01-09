package ru.bagrusss.servlets.forum;

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
            AND u.id>=10 GROUP BY u.`name` ORDER BY u.`name` LIMIT 10
         */
        StringBuilder sql = new StringBuilder("SELECT STRAIGHT_JOIN u.email FROM")
                .append(Helper.TABLE_USER).append("u FORCE INDEX(Name_id) ")
                .append("JOIN ").append(Helper.TABLE_POST)
                .append("p FORCE INDEX (ForumShortName_UserEmail) ")
                .append("ON u.email=p.user_email ")
                .append("WHERE p.forum_short_name = '")
                .append(param).append("\' ");
        param = req.getParameter("since_id");
        if (param != null) {
            sql.append(" AND u.id >= ").append(param);
        }
        sql.append(" GROUP BY `name`");
        param = req.getParameter("order");
        if (param != null) {
            sql.append(" ORDER BY u.`name` ").append(param);
        }
        param = req.getParameter("limit");
        if (param != null) {
            sql.append(" LIMIT ").append(param);
        }
        JsonArray users = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    String user = rs.getString(1);
                    if (user != null)
                        users.add(getUserDetails(connection, user, true));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), users);
    }
}
