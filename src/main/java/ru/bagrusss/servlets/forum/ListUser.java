package ru.bagrusss.servlets.forum;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */
public class ListUser extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/forum/listUsers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT * FROM `User` u JOIN `Forum` f
            ON u.email=f.user_email
            WHERE f.short_name = ?;

            SELECT * FROM `User` u JOIN `Forum` f
            ON u.email=f.user_email
            WHERE f.short_name = ? AND u.id >= ? ORDER BY u.name LIMIT ?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
