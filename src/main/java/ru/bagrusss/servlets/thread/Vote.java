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

public class Vote extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/vote/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `Thread` SET `likes`=`likes`+1 WHERE `id`=?
            UPDATE `Thread` SET `dislikes`=`dislikes`+1 WHERE `id`=?
         */
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        long id = params.get(THREAD).getAsLong();
        byte vt = params.get(VOTE).getAsByte();
        try (Connection connection = mHelper.getConnection()) {
            params = vote(connection, DBHelper.TABLE_THREAD, id, vt);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
