package ru.bagrusss.servlets.user;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav
 */
public class Unfollow extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/unfollow/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            DELETE FROM `Followers` WHERE follower_email = ?;

            те же запросы, что в user/details/  винести отдельный в метод

            SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)

        */
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
