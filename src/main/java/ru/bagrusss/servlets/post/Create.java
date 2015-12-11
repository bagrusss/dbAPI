package ru.bagrusss.servlets.post;

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
import java.util.LinkedHashMap;
import java.util.Map;

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
        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(Helper.TABLE_POST)
                .append("(`thread_id`,`message`, `user_email`, `forum_short_name`, `date`, ")
                .append("`parent`,`isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`)")
                .append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        Map<String, Object> sqlParams = new LinkedHashMap<>();

        sqlParams.put("thread", params.get("thread").getAsInt());
        sqlParams.put("message", params.get("message").getAsString());
        sqlParams.put("user", params.get("user").getAsString());
        sqlParams.put("forum", params.get("forum").getAsString());
        sqlParams.put("date", params.get("date").getAsString());
        sqlParams.put("parent", params.has("parent") ? params.get("parent").getAsInt() : null);
        sqlParams.put("isApproved", params.has("isApproved") && params.get("isApproved").getAsBoolean());
        sqlParams.put("isHighlighted", params.has("isHighlighted") && params.get("isHighlighted").getAsBoolean());
        sqlParams.put("isEdited", params.has("isEdited") && params.get("isEdited").getAsBoolean());
        sqlParams.put("isSpam", params.has("isSpam") && params.get("isSpam").getAsBoolean());
        sqlParams.put("isDeleted", params.has("isDeleted") && params.get("isDeleted").getAsBoolean());
        long id = 0;
        try {
            id = mHelper.preparedInsertAndGetID(mHelper.getConnection(), sql.toString(),
                    new ArrayList<>(sqlParams.values()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlParams.put("id", id);
        resp.setStatus(HttpServletResponse.SC_OK);
        String res = mGson.toJson(sqlParams);
        Errors.correct(resp.getWriter(), res);

    }
}
