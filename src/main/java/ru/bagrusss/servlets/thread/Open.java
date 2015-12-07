package ru.bagrusss.servlets.thread;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class Open extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/open/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            UPDATE `Thread` SET isClosed = 0 WHERE id =?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}