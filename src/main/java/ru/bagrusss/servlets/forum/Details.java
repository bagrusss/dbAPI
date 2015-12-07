package ru.bagrusss.servlets.forum;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */

public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT f.*, u.* FROM `Forum` f JOIN `User` u
              ON f.user_email=u.email WHERE f.short_name = ?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
