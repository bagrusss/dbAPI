package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav
 */

public class Update extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/update/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        long id = params.get(THREAD).getAsLong();
        /*
            UPDATE `Thread` SET `message`=?, `slug`=? WHERE id =?;
         */
        List<Object> sqlParams = new ArrayList<>();
        sqlParams.add(params.get(MESSAGE).getAsString());
        sqlParams.add(params.get(SLUG).getAsString());
        sqlParams.add(id);
        JsonObject result;
        try (Connection connection = mHelper.getConnection()) {
            String sql = "UPDATE `Thread` SET `message`=?, `slug`=? WHERE id =?";
            mHelper.runPreparedUpdate(connection, sql, sqlParams);
            result = getThreadDetails(connection, id);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), result);
    }
}
