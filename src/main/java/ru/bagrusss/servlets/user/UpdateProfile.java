package ru.bagrusss.servlets.user;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 20.10.15.
 */

public class UpdateProfile extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/updateProfile/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
            UPDATE `User` SET `email`=?, `about`=?, `name`=? WHERE `email` = ?;
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
