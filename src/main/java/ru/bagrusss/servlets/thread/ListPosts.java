package ru.bagrusss.servlets.thread;

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
 * Created by vladislav on 19.10.15.
 */
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        int thread = Integer.valueOf(req.getParameter(THREAD));
        /*
            SELECT *, likes-dislikes AS points FROM `Post` WHERE `thread_id` = ?
         */
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd ")
                .append("FROM").append(Helper.TABLE_POST)
                .append("WHERE `thread_id`=").append(thread);
        String par = req.getParameter(SINCE);
        if (par != null)
            sql.append(" AND `date` >= \'").append(par).append("\' ");
        par = req.getParameter(ORDER);
        if (par != null)
            sql.append(" ORDER BY `date` ").append(par);
        par = req.getParameter(LIMIT);
        if (par != null)
            sql.append(" LIMIT ").append(par);
        JsonArray postsList = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    postsList.add(parsePost(rs, null));
                }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), postsList);
    }
}
