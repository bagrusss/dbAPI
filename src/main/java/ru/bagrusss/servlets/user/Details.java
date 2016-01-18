package ru.bagrusss.servlets.user;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String email = req.getParameter(USER);
        /*
           SELECT * FROM `User` WHERE `email` = ?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;
           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)
           */
        JsonObject result;
        try (Connection connection = mHelper.getConnection()) {
            result = getUserDetails(connection, email);
        } catch (SQLException e) {
            e.printStackTrace();
            Errors.unknownError(resp.getWriter());
            return;
        }
        if (result == null)
            Errors.notFound(resp.getWriter());
        else Errors.correct(resp.getWriter(), result);
    }

}
