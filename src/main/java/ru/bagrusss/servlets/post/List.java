package ru.bagrusss.servlets.post;

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
    public static final String URL = BaseServlet.BASE_URL + "/post/list/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);

        /*
            SELECT *, `likes`-`dislikes` AS points FROM `Post` WHERE `forum_short_name` = ?
            SELECT *, `likes`-`dislikes` AS points FROM `Post` WHERE `thread_id` = ?

            SELECT *, `likes`-`dislikes` AS points FROM `Post`
            WHERE `thread_id` = ? AND `date` >= ? ORDER BY `date` ASC LIMIT ?

         */
        StringBuilder sql = new StringBuilder("SELECT *, `likes`-`dislikes` points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd ")
                .append("FROM").append(DBHelper.TABLE_POST)
                .append("WHERE ");
        String par = req.getParameter("forum");
        if (par != null)
            sql.append("forum_short_name =\'")
                    .append(par).append('\'');
        else
            sql.append("thread_id =")
                    .append(Long.valueOf(req.getParameter(THREAD)));
        par = req.getParameter("since");
        if (par != null)
            sql.append(" AND `date` >= \'")
                    .append(par).append('\'');
        par = req.getParameter("order");
        if (par != null)
            sql.append(" ORDER BY `date` ").append(par);
        par = req.getParameter("limit");
        if (par != null)
            sql.append(" LIMIT ").append(par);
        JsonArray posts = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    posts.add(parsePost(rs, null));
                }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), posts);
    }
}
