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
 * Created by vladislav
 */

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/create/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            INSERT IGNORE INTO `Thread` (`forum`, `title`, `slug`, `user_email`, `date`, `message`, `isClosed`, `isDeleted`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
         */
        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(Helper.TABLE_THREAD)
                .append("(`forum`, `title`, `slug`, `user_email`, `date`, `message`, `isClosed`, `isDeleted`) ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(8);
        try {
            sqlParams.add(params.get("forum").getAsString());
            sqlParams.add(params.get("title").getAsString());
            sqlParams.add(params.get("slug").getAsString());
            sqlParams.add(params.get("user").getAsString());
            sqlParams.add(params.get("date").getAsString());
            sqlParams.add(params.get("message").getAsString());
            sqlParams.add(params.get("isClosed").getAsBoolean());
            sqlParams.add(params.has("isDeleted") && params.get("isDeleted").getAsBoolean());
        } catch (UnsupportedOperationException e) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Errors.incorrecRequest(resp.getWriter());
        }
        try {
            long id = mHelper.preparedInsertAndGetKeys(mHelper.getConnection(), sql.toString(), sqlParams);
            params.addProperty("id", id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);
    }
}
