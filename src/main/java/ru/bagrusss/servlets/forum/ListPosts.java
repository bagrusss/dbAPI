package ru.bagrusss.servlets.forum;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Errors;
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


public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listPosts/";


    @Override
    @SuppressWarnings("OverlyComplexMethod")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, `likes`-`dislikes` points FROM `Posts` WHERE `forum_short_name` = ?
         */
        String[] related = req.getParameterValues(RELATED);
        boolean user = false;
        boolean forum = false;
        boolean thread = false;
        if (related != null) {
            for (String rel : related) {
                switch (rel) {
                    case USER:
                        user = true;
                        break;
                    case FORUM:
                        forum = true;
                        break;
                    case THREAD:
                        thread = true;
                        break;
                    default:
                        Errors.incorrecRequest(resp.getWriter());
                        return;
                }
            }
        }
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd ")
                .append("FROM").append(DBHelper.TABLE_POST);
        String par = req.getParameter(FORUM);
        sql.append("WHERE `forum_short_name` = \'").append(par).append('\'');
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
            final boolean finalThread = thread;
            final boolean finalUser = user;
            final boolean finalForum = forum;
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject pst = new JsonObject();
                    pst.addProperty(ID, rs.getLong(1));
                    pst.addProperty(IS_APPROVED, rs.getBoolean(IS_APPROVED));
                    pst.addProperty(IS_DELETED, rs.getBoolean(IS_DELETED));
                    pst.addProperty(IS_EDITED, rs.getBoolean(IS_EDITED));
                    pst.addProperty(IS_HIGHLIGHTED, rs.getBoolean(IS_HIGHLIGHTED));
                    pst.addProperty(IS_SPAM, rs.getBoolean(IS_SPAM));
                    pst.addProperty(MESSAGE, rs.getString(MESSAGE));
                    if (finalThread)
                        pst.add(THREAD, getThreadDetails(connection, rs.getLong("thread_id")));
                    else pst.addProperty(THREAD, rs.getLong("thread_id"));
                    pst.addProperty(DATE, rs.getString("pd"));
                    if (!finalUser)
                        pst.addProperty(USER, rs.getString("user_email"));
                    else pst.add(USER, getUserDetails(connection, rs.getString("user_email")));
                    if (!finalForum)
                        pst.addProperty(FORUM, rs.getString("forum_short_name"));
                    else pst.add(FORUM, getForumDetails(connection, rs.getString("forum_short_name")));
                    pst.addProperty(DISLIKES, rs.getLong(DISLIKES));
                    pst.addProperty(LIKES, rs.getLong(LIKES));
                    pst.addProperty(POINTS, rs.getLong(POINTS));
                    long parent = rs.getLong(PARENT);
                    pst.addProperty(PARENT, parent == 0 ? null : parent);
                    result.add(pst);
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
