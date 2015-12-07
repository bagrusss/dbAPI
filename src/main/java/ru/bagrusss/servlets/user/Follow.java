package ru.bagrusss.servlets.user;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 20.10.15.
 */

public class Follow extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/follow/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
           INSERT IGNORE INTO(`follower_email`, `following_email`) VALUES (? ,?);

           те же запросы, что в user/details/

           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)
         */
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
