package ru.bagrusss.servlets.forum;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class ListThreads extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listThreads/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT *, `likes`-`dislikes` points FROM `Thread` WHERE `forum_short_name` = ?

            если forum и user

            SELECT *, t.`likes`-`dislikes` t_points FROM `Thread` t
            JOIN `User` u ON u.`email` = t.`user_email`
            JOIN `Forum` f ON f.`short_name` = t.`forum` WHERE p.`forum_short_name` = ?
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
