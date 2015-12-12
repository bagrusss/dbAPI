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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 19.10.15.
 */
public class Subscrabe extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/subscribe/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);

        /*
            INSERT IGNORE `Subscriptions` (`user_email`, `thread_id`) VALUES (?, ?)
         */

        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(2);
        sqlParams.add(params.get("user").getAsString());
        sqlParams.add(params.get("thread").getAsInt());
        StringBuilder sql = new StringBuilder("INSERT IGNORE ").append(Helper.TABLE_SUBSCRIPTIONS)
                .append("VALUES (?, ?)");
        try {
            mHelper.runPreparedUpdate(mHelper.getConnection(), sql.toString(), sqlParams);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);

    }
}
