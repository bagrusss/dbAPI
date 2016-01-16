package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
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
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(8);
        try {
            sqlParams.add(params.get(FORUM).getAsString());
            sqlParams.add(params.get(TITLE).getAsString());
            sqlParams.add(params.get(SLUG).getAsString());
            sqlParams.add(params.get(USER).getAsString());
            sqlParams.add(params.get(DATE).getAsString());
            sqlParams.add(params.get(MESSAGE).getAsString());
            sqlParams.add(params.get(IS_CLOSED).getAsBoolean());
            sqlParams.add(params.has(IS_DELETED) && params.get(IS_DELETED).getAsBoolean());
        } catch (UnsupportedOperationException e) {
            Errors.incorrecRequest(resp.getWriter());
            return;
        }
        try (Connection connection = mHelper.getConnection()) {
            long id = mHelper.preparedInsertAndGetKeys(connection, sql.toString(), sqlParams);
            params.addProperty(ID, id);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
