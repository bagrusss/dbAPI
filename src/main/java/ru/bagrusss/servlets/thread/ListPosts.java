package ru.bagrusss.servlets.thread;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT *, likes-dislikes AS points FROM `Post` WHERE `thread_id` = ?
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
