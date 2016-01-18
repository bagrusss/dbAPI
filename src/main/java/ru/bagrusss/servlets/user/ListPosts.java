package ru.bagrusss.servlets.user;

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
 * Created by vladislav on 20.10.15.
 */
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT * FROM `Post` WHERE `user_email`=? AND date >= ? ORDER BY `date` ASC LIMIT ?;
         */
        String par = req.getParameter(USER);
        StringBuilder sql = new StringBuilder("SELECT *, DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd, ")
                .append("likes-CAST(dislikes AS SIGNED) points FROM")
                .append(DBHelper.TABLE_POST)
                .append("WHERE `user_email` = \'")
                .append(par).append("\' ");
        par = req.getParameter(SINCE);
        if (par != null)
            sql.append(" AND `date` >= \'")
                    .append(par).append("\' ");
        par = req.getParameter(ORDER);
        if (par != null)
            sql.append(" ORDER BY `date` ").append(par);
        par = req.getParameter(LIMIT);
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
