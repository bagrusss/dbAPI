package ru.bagrusss.servlets.post;

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

    public static final String URL = BaseServlet.BASE_URL + "/post/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, likes-dislikes AS points FROM `Post` p WHERE p.id =?;
            SELECT * FROM `User` WHERE `email` = ?;
            SELECT * FROM `Forum` WHERE `short_name` = ?;
            SELECT *, likes-dislikes AS points FROM `Thread` WHERE `id` = ?
         */
        long id = Long.valueOf(req.getParameter(POST));
        JsonObject result;
        try (Connection connection = mHelper.getConnection()) {
            result = getPostDetails(connection, id);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        if (result != null)
            Errors.correct(resp.getWriter(), result);
        else Errors.notFound(resp.getWriter());
    }
}
