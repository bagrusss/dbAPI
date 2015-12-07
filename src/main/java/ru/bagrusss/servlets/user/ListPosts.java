package ru.bagrusss.servlets.user;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 20.10.15.
 */
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listPosts/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            SELECT * FROM `Post` WHERE `user_email`=?;

            SELECT * FROM `Post` WHERE `user_email`=? AND date >= ?;

            SELECT * FROM `Post` WHERE `user_email`=? AND date >= ? ORDER BY `date` ASC LIMIT ?;

         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
