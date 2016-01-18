package ru.bagrusss.servlets.thread;

import com.google.gson.JsonArray;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.DBHelper;
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

public class List extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/list/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, likes-dislakes as points FROM `Thread` WHERE `forum` = ?;
            SELECT *, likes-dislakes as points FROM `Thread` WHERE `user_email` = ?;
            SELECT *, likes-dislakes as points FROM `Thread` WHERE `user_email` = ? AND `date` >= ?
                ORDER BY `date` LIMIT ?;
         */
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(`date`, '%Y-%m-%d %H:%i:%s') dt FROM")
                .append(DBHelper.TABLE_THREAD)
                .append("WHERE ");
        String par = req.getParameter(USER);
        if (par != null) {
            sql.append("`user_email`=\'")
                    .append(par).append('\'');
        } else {
            sql.append("`forum`= \'")
                    .append(req.getParameter(FORUM)).append('\'');
        }
        par = req.getParameter(SINCE);
        if (par != null)
            sql.append(" AND `date` >= \'")
                    .append(par).append('\'');
        par = req.getParameter(ORDER);
        if (par != null)
            sql.append(" ORDER BY `date` ")
                    .append(par);
        par = req.getParameter(LIMIT);
        if (par != null)
            sql.append(" LIMIT ").append(par);
        JsonArray threads = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    threads.add(parseThread(rs, null));
                }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), threads);
    }
}
