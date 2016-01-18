package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.DBHelper;
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

public class Remove extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/remove/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        /*
            UPDATE `Thread` SET isDeleted = 1,  WHERE id =?;
         */
        try (Connection connection = mHelper.getConnection()) {
            long id = params.get(THREAD).getAsLong();
            mHelper.runUpdate(connection,
                    "UPDATE " + DBHelper.TABLE_THREAD + " SET posts=0, isDeleted=1 WHERE id=" + id);
            mHelper.runUpdate(connection,
                    "UPDATE " + DBHelper.TABLE_POST + " SET isDeleted=1 WHERE thread_id=" + id);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
