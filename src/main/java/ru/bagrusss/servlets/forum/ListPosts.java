package ru.bagrusss.servlets.forum;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */

public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT *, `likes`-`dislikes` points FROM `Posts` WHERE `forum_short_name` = ?

            если forum user thread

            SELECT *, p.`likes`-p.`dislikes` p_points, t.`likes`-t.`dislikes` t_points FROM `Post` p
            JOIN `User` u ON u.`email` = p.`user_email`
            JOIN `Forum` f ON f.`short_name` = p.`forum_short_name`
            JOIN `Thread` t ON t.`forum`=p.`forum_short_name` WHERE p.`forum_short_name` = ?

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
