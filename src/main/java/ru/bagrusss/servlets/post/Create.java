package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav
 */

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/create/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        /*
            INSERT IGNORE INTO `Post` (`thread_id`,`message`, `user_email`, `forum_short_name`, `date`
                `parent`,`isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
         */

        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(Helper.TABLE_POST)
                .append("(`thread_id`,`message`, `user_email`, `forum_short_name`, `date`, ");
        List<Object> sqlParams = new ArrayList<>(11);
        long thread;
        sqlParams.add(thread = params.get(THREAD).getAsLong());
        sqlParams.add(params.get(MESSAGE).getAsString());
        sqlParams.add(params.get(USER).getAsString());
        sqlParams.add(params.get(FORUM).getAsString());
        sqlParams.add(params.get(DATE).getAsString());
        byte count = 5;
        if (params.has(PARENT)) {
            try {
                sqlParams.add(params.get(PARENT).getAsLong());
            } catch (UnsupportedOperationException e) {
                sqlParams.add(null);
            }
            sql.append("`parent`, ");
            ++count;
        }
        if (params.has(IS_APPROVED)) {
            sqlParams.add(params.get(IS_APPROVED).getAsBoolean());
            sql.append("`isApproved`, ");
            ++count;
        }
        if (params.has(IS_HIGHLIGHTED)) {
            sqlParams.add(params.get(IS_HIGHLIGHTED).getAsBoolean());
            sql.append("`isHighlighted`, ");
            ++count;
        }
        if (params.has(IS_EDITED)) {
            sqlParams.add(params.get(IS_EDITED).getAsBoolean());
            sql.append("`isEdited`, ");
            ++count;
        }
        if (params.has(IS_SPAM)) {
            sqlParams.add(params.get(IS_SPAM).getAsBoolean());
            sql.append("`isSpam`, ");
            ++count;
        }
        boolean isDeleted = false;
        if (params.has(IS_DELETED)) {
            sqlParams.add(isDeleted = params.get(IS_DELETED).getAsBoolean());
            sql.append("`isDeleted`");
            ++count;
        }
        sql.append(") VALUES (");
        for (byte i = 0; i < count - 1; ++i) {
            sql.append("?, ");
        }
        sql.append("? )");
        try (Connection connection = mHelper.getConnection()) {
            mHelper.preparedInsertAndGetKeys(connection, sql.toString(), sqlParams, gk -> {
                ResultSetMetaData metaData = gk.getMetaData();
                int columns = metaData.getColumnCount();
                if (gk.next()) {
                    params.addProperty(ID, gk.getLong(1));
                    for (byte i = 2; i <= columns; ++i) {
                        Object obj = gk.getObject(i);
                        params.add(metaData.getColumnName(i), mGSON.toJsonTree(obj == null ? null
                                : gk.getObject(i)));
                    }
                }
            });
            if (!isDeleted) {
                mHelper.runUpdate(connection, "Update Thread SET posts=posts+1 WHERE id=" + thread);
            }
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
