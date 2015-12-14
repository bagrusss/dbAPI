package ru.bagrusss.servlets.forum;

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

    public static final String URL = BaseServlet.BASE_URL + "/forum/create/";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        resp.setCharacterEncoding(DEFAULT_ENCODING);

        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>(3);
        String name = params.get("name").getAsString();
        String shortname = params.get("short_name").getAsString();
        String user = params.get("user").getAsString();
        sqlBuilder.setLength(0);
        sqlBuilder.append("INSERT IGNORE INTO ").append(Helper.TABLE_FORUM)
                .append("(`name`, `short_name`, `user_email`)")
                .append(" VALUES (?,?,?)");
        sqlParams.add(name);
        sqlParams.add(shortname);
        sqlParams.add(user);
        long id = 0;
        try {
            id = mHelper.preparedInsertAndGetKeys(mHelper.getConnection(), sqlBuilder.toString(), sqlParams);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        params.addProperty("id", id);
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);

    }
}
