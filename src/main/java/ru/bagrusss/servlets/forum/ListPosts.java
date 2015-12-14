package ru.bagrusss.servlets.forum;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String[] related = req.getParameterValues("related");
        /*
            SELECT *, `likes`-`dislikes` points FROM `Posts` WHERE `forum_short_name` = ?

            если forum user thread

            SELECT *, p.`likes`-p.`dislikes` p_points, t.`likes`-t.`dislikes` t_points FROM `Post` p
            JOIN `User` u ON u.`email` = p.`user_email`
            JOIN `Forum` f ON f.`short_name` = p.`forum_short_name`
            JOIN `Thread` t ON t.`forum`=p.`forum_short_name` WHERE t.`forum_short_name` = ?

         */
        StringBuilder sql = new StringBuilder("SELECT p.id pid, p.date, pdate, p.isApproved pisApp, ")
                .append("p.isDeleted pisDel, p.isEdited pisEd, p.isHighlighted pisHig, p.isSpam pisSpam, ")
                .append("p.message pmess, p.likes plik, p.dislikes pdis, p.thread_id ptid, ")
                .append("p.user_email puser, p.forum_short_name pforum, p.parent ppar ")
                .append("FROM `Post` p ");
        boolean isForum = false;
        boolean isThread = false;
        if (related != null) {
            for (String rel : related) {
                switch (rel) {
                    case "forum":
                        sql.append("JOIN `Forum` f ON f.`short_name` = p.`forum_short_name ");
                        isForum = true;
                        break;
                    case "thread":
                        sql.append("JOIN `Thread` t ON t.`forum` = p.`forum_short_name ");
                        isThread = true;
                        break;
                    case "user":

                        break;
                    default:
                        resp.setStatus(HttpServletResponse.SC_OK);
                        Errors.incorrecRequest(resp.getWriter());
                        return;
                }
            }
        }
        String forum = req.getParameter("forum");
        sql.append("WHERE p.`forum_short_name` = \'").append(forum).append('\'');
        /*JsonArray resulr = new JsonArray();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), rs -> {
                while (rs.next()) {
                    JsonObject item = new JsonObject();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
