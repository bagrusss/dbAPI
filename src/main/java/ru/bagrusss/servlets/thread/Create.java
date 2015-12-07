package ru.bagrusss.servlets.thread;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/create/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            INSERT IGNORE INTO `Thread` (`forum`, `title`, `slug`, `user_email`, `date`, `message`, `isClosed`, `isDeleted`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
