package ru.bagrusss.servlets.thread;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */

public class Restore extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/restore/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            UPDATE `Thread` SET isDeleted = 0 WHERE id =?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
