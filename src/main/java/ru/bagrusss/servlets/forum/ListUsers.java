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
        String param = req.getParameter(FORUM);
        /*
            SELECT STRAIGHT_JOIN u.email FROM User u FORCE INDEX(Name_id)
            JOIN Post p FORCE INDEX (ForumShortName_UserEmail)
            ON u.email=p.user_email WHERE p.forum_short_name = ""
            AND u.id>=10 ORDER BY u.`name` LIMIT 10

            SELECT user_email FROM Post WHERE forum_short_name="aaa";
            SELECT * FROM User WHERE email IN ();
         */
        StringBuilder sql = new StringBuilder("SELECT user_email FROM")
                .append(Helper.TABLE_POST)
                .append("FORCE INDEX (ForumShortName_UserEmail) ")
                .append("WHERE forum_short_name=\'").append(param).append('\'');
        JsonArray usrs = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            String users = mHelper.runTypedQuery(connection, sql.toString(), rs -> {
                StringBuilder builder = new StringBuilder();
                while (rs.next()) {
                    builder.append(rs.getString(1)).append("\',\'");
                }
                if (builder.length() > 0) {
                    builder.replace(builder.length() - 3, builder.length(), "");
                }
                return builder.toString();
            });
            if (users.isEmpty()) {
                Errors.correct(resp.getWriter(), usrs);
                return;
            }
            sql.setLength(0);
            sql.append("SELECT * FROM").append(Helper.TABLE_USER)
                    .append("FORCE INDEX (Name_email_id)")
                    .append(" WHERE email IN (\'").append(users).append("\')");
            param = req.getParameter(SINCE_ID);
            if (param != null) {
                sql.append(" AND id >= ").append(param);
            }
            sql.append(" ORDER BY `name` ");
            param = req.getParameter(ORDER);
            if (param != null) {
                sql.append(param);
            }
            param = req.getParameter(LIMIT);
            if (param != null) {
                sql.append(" LIMIT ").append(param);
            }
            mHelper.runQuery(connection, sql.toString(), rs -> {
                prepareUsers(connection, rs, usrs);
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), usrs);
    }
}
