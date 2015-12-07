package ru.bagrusss.servlets.user;

import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 20.10.15.
 */
public class ListFollowers extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/listFollowers/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           или
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? AND ?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ? ORDER BY `name` ? LIMIT ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?;
         */
    }
}
