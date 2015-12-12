package ru.bagrusss.servlets.thread;

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

        StringBuilder sql = new StringBuilder("SELECT t.*, t.likes-t.dislikes points, ")
                .append("DATE_FORMAT(t.date, '%Y-%m-%d %H:%i:%s') ")
                .append("dt, COUNT(p.id) posts FROM")
                .append(Helper.TABLE_THREAD)
                .append("t, ").append(Helper.TABLE_POST)
                .append("p WHERE ");
        String par = req.getParameter("user");
        if (par != null) {
            sql.append("t.`user_email`=\'")
                    .append(par);
        } else {
            sql.append("t.`forum`= \'")
                    .append(req.getParameter("forum"));
        }
        sql.append("\' AND p.isDeleted = False ");
        par = req.getParameter("since");
        if (par != null)
            sql.append(" AND t.`date` >= \'")
                    .append(par).append('\'');
        par = req.getParameter("order");
        if (par != null)
            sql.append(" ORDER BY t.`date` ")
                    .append(par);
        par = req.getParameter("limit");
        if (par != null)
            sql.append(" LIMIT ").append(par);
        resp.setStatus(HttpServletResponse.SC_OK);
        JsonArray threads = new JsonArray();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject curent = parseThread(rs);
                    threads.add(curent);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), threads);
    }
}
