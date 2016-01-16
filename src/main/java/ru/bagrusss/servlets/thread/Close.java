package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
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

public class Close extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/close/";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `Thread` SET isClosed = 1 WHERE id =?;
         */
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        try (Connection connection = mHelper.getConnection()) {
            toggleField(connection, Helper.TABLE_THREAD,
                    params.get(THREAD).getAsLong(), IS_CLOSED, true);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
