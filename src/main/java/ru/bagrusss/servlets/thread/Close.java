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

public class Close extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/close/";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `Thread` SET isClosed = 1 WHERE id =?;
         */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE").append(Helper.TABLE_THREAD)
                .append("SET `isClosed` = 1 WHERE id = ").append(params.get("thread").getAsBigInteger());
        try {
            if (mHelper.runUpdate(mHelper.getConnection(), sqlBuilder.toString()) == 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                Errors.notFound(resp.getWriter());
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);
    }
}
