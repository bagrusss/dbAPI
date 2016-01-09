package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
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
 * Created by vladislav on 19.10.15.
 */
public class Update extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/update/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        /*
            UPDATE `Post` SET `message`=? WHERE id =?;
         */
        List<Object> sqlParams = new ArrayList<>(2);
        sqlParams.add(params.get("message").getAsString());
        long id;
        sqlParams.add(id = params.get("post").getAsLong());
        JsonObject result = null;
        try (Connection connection = mHelper.getConnection()) {
            String sql = "UPDATE `Post` SET `message`=? WHERE id =?;";
            mHelper.runPreparedUpdate(connection, sql, sqlParams);
            result = getPostDetails(connection, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        if (result != null)
            Errors.correct(resp.getWriter(), result);
        else Errors.notFound(resp.getWriter());

    }
}
