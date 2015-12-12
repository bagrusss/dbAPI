package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 19.10.15.
 */

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/create/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        /*
            INSERT IGNORE INTO `Post` (`thread_id`,`message`, `user_email`, `forum_short_name`, `date`
                `parent`,`isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
         */
/*        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(Helper.TABLE_POST)
                .append("(`thread_id`,`message`, `user_email`, `forum_short_name`, `date`, ")
                .append("`parent`,`isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`)")
                .append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");*/
        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(Helper.TABLE_POST)
                .append("(`thread_id`,`message`, `user_email`, `forum_short_name`, `date`, ");
        List<Object> sqlParams = new ArrayList<>(11);
        sqlParams.add(params.get("thread").getAsLong());
        sqlParams.add(params.get("message").getAsString());
        sqlParams.add(params.get("user").getAsString());
        sqlParams.add(params.get("forum").getAsString());
        sqlParams.add(params.get("date").getAsString());
        byte count = 5;
        if (params.has("parent")) {
            sqlParams.add(params.get("parent").getAsInt());
            sql.append("`parent`, ");
            ++count;
        }
        if (params.has("isApproved")) {
            sqlParams.add(params.get("isApproved").getAsBoolean());
            sql.append("`isApproved`, ");
            ++count;
        }
        if (params.has("isHighlighted")) {
            sqlParams.add(params.get("isHighlighted").getAsBoolean());
            sql.append("`isHighlighted`, ");
            ++count;
        }
        if (params.has("isEdited")) {
            sqlParams.add(params.get("isEdited").getAsBoolean());
            sql.append("`isEdited`, ");
            ++count;
        }
        if (params.has("isSpam")) {
            sqlParams.add(params.get("isSpam").getAsBoolean());
            sql.append("`isSpam`, ");
            ++count;
        }
        if (params.has("isDeleted")) {
            sqlParams.add(params.get("isDeleted").getAsBoolean());
            sql.append("`isDeleted`");
            ++count;
        }
        sql.append(") VALUES (");
        for (byte i = 0; i < count - 1; ++i) {
            sql.append("?, ");
        }
        sql.append("? )");

        try {
            mHelper.preparedInsertAndGetKeys(mHelper.getConnection(), sql.toString(), sqlParams, gk -> {
                ResultSetMetaData metaData = gk.getMetaData();
                int columns = metaData.getColumnCount();
                if (gk.next()) {
                    params.addProperty("id", gk.getLong(1));
                    for (byte i = 2; i <= columns; ++i) {
                        Object obj= gk.getObject(i);
                        params.add(metaData.getColumnName(i), mGson.toJsonTree(obj == null ? null : gk.getObject(i)));
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);

    }
}
