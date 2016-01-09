package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
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
            UPDATE `Thread` SET isDeleted = 1,  WHERE id =?;
         */
        try {
            long id = params.get("thread").getAsLong();
            mHelper.runUpdate(mHelper.getConnection(),
                    "UPDATE " + Helper.TABLE_THREAD + " SET posts=0, isDeleted=1 WHERE id=" + id);
            mHelper.runUpdate(mHelper.getConnection(),
                    "UPDATE " + Helper.TABLE_POST + " SET isDeleted=1 WHERE thread_id=" + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);
    }
}
