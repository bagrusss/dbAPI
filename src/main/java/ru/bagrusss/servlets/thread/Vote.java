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

public class Vote extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/vote/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `Thread` SET `likes`=`likes`+1 WHERE `id`=?
            UPDATE `Thread` SET `dislikes`=`dislikes`+1 WHERE `id`=?
         */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        long id = params.get("thread").getAsLong();
        byte vote = params.get("vote").getAsByte();

        try {
            params = this.vote(Helper.TABLE_THREAD, id, vote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        if (params != null) {
            Errors.correct(resp.getWriter(), params);
        }
    }
}
