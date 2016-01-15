package ru.bagrusss.servlets.forum;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

public class ListThreads extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listThreads/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, `likes`-`dislikes` points FROM `Posts` WHERE `forum_short_name` = ?
         */
        String[] related = req.getParameterValues("related");
        boolean user = false;
        boolean forum = false;

        if (related != null) {
            for (String rel : related) {
                switch (rel) {
                    case "user":
                        user = true;
                        break;
                    case "forum":
                        forum = true;
                        break;
                    default:
                        resp.setStatus(HttpServletResponse.SC_OK);
                        Errors.incorrecRequest(resp.getWriter());
                        return;
                }
            }
        }
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') td ")
                .append("FROM").append(Helper.TABLE_THREAD);
        String par = req.getParameter("forum");
        sql.append("WHERE `forum` = \'")
                .append(par).append('\'');
        par = req.getParameter("since");
        if (par != null) {
            sql.append(" AND `date` >= \'")
                    .append(par).append("\' ");
        }
        par = req.getParameter("order");
        if (par != null) {
            sql.append("ORDER BY `date` ").append(par);
        }
        par = req.getParameter("limit");
        if (par != null) {
            sql.append(" LIMIT ").append(par);
        }
        JsonArray result = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            final boolean finalUser = user;
            final boolean finalForum = forum;
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject thr = new JsonObject();
                    thr.addProperty("id", rs.getLong(1));
                    thr.addProperty("forum", rs.getLong("forum_id"));
                    thr.addProperty("isDeleted", rs.getBoolean("isDeleted"));
                    thr.addProperty("isClosed", rs.getBoolean("isClosed"));
                    thr.addProperty("message", rs.getString("message"));
                    thr.addProperty("slug", rs.getString("slug"));
                    thr.addProperty("title", rs.getString("title"));
                    thr.addProperty("date", rs.getString("td"));
                    if (!finalUser)
                        thr.addProperty("user", rs.getString("user_email"));
                    else thr.add("user", getUserDetails(connection, rs.getString("user_email"), true));
                    if (!finalForum)
                        thr.addProperty("forum", rs.getString("forum"));
                    else thr.add("forum", getForumDetails(connection, rs.getString("forum")));
                    thr.addProperty("dislikes", rs.getLong("dislikes"));
                    thr.addProperty("likes", rs.getLong("likes"));
                    thr.addProperty("points", rs.getLong("points"));
                    thr.addProperty("posts", rs.getLong("posts"));
                    result.add(thr);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);
    }
}
