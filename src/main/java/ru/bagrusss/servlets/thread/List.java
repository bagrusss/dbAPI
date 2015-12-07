package ru.bagrusss.servlets.thread;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class List extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/list/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT *, likes-dislakes as points FROM `Thread` WHERE `forum` = ?;
            SELECT *, likes-dislakes as points FROM `Thread` WHERE `user_email` = ?;

            SELECT *, likes-dislakes as points FROM `Thread` WHERE `user_email` = ? AND `date` >= ?
                ORDER BY `date` LIMIT ?;

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
