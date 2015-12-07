package ru.bagrusss.servlets.post;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class List extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/list/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT *, `likes`-`dislikes` AS points FROM `Post` WHERE `forum_short_name` = ?
            SELECT *, `likes`-`dislikes` AS points FROM `Post` WHERE `thread_id` = ?

            SELECT *, `likes`-`dislikes` AS points FROM `Post`
            WHERE `thread_id` = ? AND `date` >= ? ORDER BY `date` ASC LIMIT ?

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
