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
public class Remove extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/remove/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        /*
            UPDATE `Thread` SET isDeleted = 1 WHERE id =?;
         */
        try {
            toggleThreadField(params.get("thread").getAsLong(), "isDeleted", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params.toString());
    }
}
