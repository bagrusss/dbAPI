package ru.bagrusss.servlets.post;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
            SELECT *, likes-likes AS points FROM `Post` p WHERE p.id =?;
            SELECT * FROM `User` WHERE `email` = ?;
            SELECT * FROM `Forum` WHERE `short_name` = ?;
            SELECT *, likes-dislikes AS points FROM `Thread` WHERE `id` = ?

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
