package ru.bagrusss.servlets.post;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */
public class Update extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/update/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            UPDATE `Post` SET `message`=? WHERE id =?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
