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
        String[] related = req.getParameterValues(RELATED);
        boolean user = false;
        boolean forum = false;

        if (related != null) {
            for (String rel : related) {
                switch (rel) {
                    case USER:
                        user = true;
                        break;
                    case FORUM:
                        forum = true;
                        break;
                    default:
                        Errors.incorrecRequest(resp.getWriter());
                        return;
                }
            }
        }
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') td ")
                .append("FROM").append(Helper.TABLE_THREAD);
        String par = req.getParameter(FORUM);
        sql.append("WHERE `forum` = \'")
                .append(par).append('\'');
        par = req.getParameter(SINCE);
        if (par != null) {
            sql.append(" AND `date` >= \'")
                    .append(par).append("\' ");
        }
        par = req.getParameter(ORDER);
        if (par != null) {
            sql.append("ORDER BY `date` ").append(par);
        }
        par = req.getParameter(LIMIT);
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
                    thr.addProperty(ID, rs.getLong(1));
                    thr.addProperty(FORUM, rs.getLong("forum_id"));
                    thr.addProperty(IS_DELETED, rs.getBoolean(IS_DELETED));
                    thr.addProperty(IS_CLOSED, rs.getBoolean(IS_CLOSED));
                    thr.addProperty(MESSAGE, rs.getString(MESSAGE));
                    thr.addProperty(SLUG, rs.getString(SLUG));
                    thr.addProperty(TITLE, rs.getString(TITLE));
                    thr.addProperty(DATE, rs.getString("td"));
                    if (!finalUser)
                        thr.addProperty(USER, rs.getString("user_email"));
                    else thr.add(USER, getUserDetails(connection, rs.getString("user_email"), true));
                    if (!finalForum)
                        thr.addProperty(FORUM, rs.getString(FORUM));
                    else thr.add(FORUM, getForumDetails(connection, rs.getString(FORUM)));
                    thr.addProperty(DISLIKES, rs.getLong(DISLIKES));
                    thr.addProperty(LIKES, rs.getLong(LIKES));
                    thr.addProperty(POINTS, rs.getLong(POINTS));
                    thr.addProperty(POSTS, rs.getLong(POSTS));
                    result.add(thr);
                }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), result);
    }
}
