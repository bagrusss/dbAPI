package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public class Restore extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/restore/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            UPDATE `Thread` SET isDeleted = 0 WHERE id =?;
         */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);

        long id = params.get("thread").getAsLong();
        String sql = "UPDATE `Thread` SET isDeleted = 0 WHERE id = " + id;
        JsonObject response = new JsonObject();
        try {
            mHelper.runUpdate(mHelper.getConnection(), sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.addProperty("thread", id);
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), response);
    }

}
