package ru.bagrusss.servlets.forum;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vladislav.
 */

public class ForumCreate extends BaseServlet {

    public static final String URL = BaseServlet.BASE_URL + "/forum/create/";
    StringBuilder mSQLStringBuilder = new StringBuilder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader body = req.getReader();
        JsonObject params = mGson.fromJson(body, JsonObject.class);

        List<String> sqlParams = new LinkedList<>();
        String name = params.get("name").getAsString();
        String shortname = params.get("short_name").getAsString();
        String user = params.get("user").getAsString();
        body.close();
        mSQLStringBuilder.setLength(0);
        mSQLStringBuilder.append("INSERT IGNORE INTO ").append(Helper.TABLE_FORUM)
                .append("(`name`, `short_name`, `user_email`)")
                .append(" VALUES (?,?,?);");
        sqlParams.add(name);
        sqlParams.add(shortname);
        sqlParams.add(user);
        try {
            mHelper.runPreparedUpdate(mHelper.getConnection(), mSQLStringBuilder.toString(), sqlParams);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlParams.clear();
        mSQLStringBuilder.setLength(0);
        mSQLStringBuilder.append("SELECT `id` FROM ").append(Helper.TABLE_FORUM)
                .append("WHERE `short_name` = ?;");
        JsonObject result = new JsonObject();
        sqlParams.add(shortname);
        try {
            mHelper.runPreparedQuery(mHelper.getConnection(), mSQLStringBuilder.toString(), sqlParams, rs -> {
                if (rs.next())
                    result.addProperty("id", rs.getInt("id"));
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlParams.clear();
        result.addProperty("short_name", shortname);
        result.addProperty("name", name);
        result.addProperty("user", user);
        JsonObject respone = new JsonObject();
        respone.addProperty("code", CODE_OK);
        respone.add("response", result);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        resp.getWriter().write(respone.toString());
    }
}
