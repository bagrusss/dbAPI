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

@SuppressWarnings("OverlyComplexMethod")
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, `likes`-`dislikes` points FROM `Posts` WHERE `forum_short_name` = ?
         */
        String[] related = req.getParameterValues("related");
        boolean user = false;
        boolean forum = false;
        boolean thread = false;
        if (related != null) {
            for (String rel : related) {
                switch (rel) {
                    case "user":
                        user = true;
                        break;
                    case "forum":
                        forum = true;
                        break;
                    case "thread":
                        thread = true;
                        break;
                    default:
                        resp.setStatus(HttpServletResponse.SC_OK);
                        Errors.incorrecRequest(resp.getWriter());
                        return;
                }
            }
        }
        StringBuilder sql = new StringBuilder("SELECT *, likes-CAST(dislikes AS SIGNED) points, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd ")
                .append("FROM").append(Helper.TABLE_POST);
        String par = req.getParameter("forum");
        sql.append("WHERE `forum_short_name` = \'").append(par).append('\'');
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
            final boolean finalThread = thread;
            final boolean finalUser = user;
            final boolean finalForum = forum;
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject pst = new JsonObject();
                    pst.addProperty("id", rs.getLong(1));
                    pst.addProperty("isApproved", rs.getBoolean("isApproved"));
                    pst.addProperty("isDeleted", rs.getBoolean("isDeleted"));
                    pst.addProperty("isEdited", rs.getBoolean("isEdited"));
                    pst.addProperty("isHighlighted", rs.getBoolean("isHighlighted"));
                    pst.addProperty("isSpam", rs.getBoolean("isSpam"));
                    pst.addProperty("message", rs.getString("message"));
                    if (finalThread)
                        pst.add("thread", getThreadDetails(connection, rs.getLong("thread_id")));
                    else pst.addProperty("thread", rs.getLong("thread_id"));
                    pst.addProperty("date", rs.getString("pd"));
                    if (!finalUser)
                        pst.addProperty("user", rs.getString("user_email"));
                    else pst.add("user", getUserDetails(connection, rs.getString("user_email"), true));
                    if (!finalForum)
                        pst.addProperty("forum", rs.getString("forum_short_name"));
                    else pst.add("forum", getForumDetails(connection, rs.getString("forum_short_name")));
                    pst.addProperty("dislikes", rs.getLong("dislikes"));
                    pst.addProperty("likes", rs.getLong("likes"));
                    pst.addProperty("points", rs.getLong("points"));
                    long parent = rs.getLong("parent");
                    pst.addProperty("parent", parent == 0 ? null : parent);
                    result.add(pst);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);
    }
}
