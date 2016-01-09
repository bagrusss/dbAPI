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
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vladislav on 19.10.15.
 */
public class Restore extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/restore/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        long id = params.get("post").getAsLong();
        /*
              UPDATE `Post` SET `isDeleted`=0 WHERE `id`=?;
         */
        try (Connection connection = mHelper.getConnection()) {
            toggleField(connection, Helper.TABLE_POST, id, "isDeleted", false);
            mHelper.runQuery(connection, "SELECT `thread_id` FROM Post Where id=" + id, rs -> {
                if (rs.next())
                    try (Statement st = connection.createStatement()) {
                        String update = "UPDATE `Thread` SET `posts`=`posts`+1 WHERE `id`=";
                        st.executeUpdate(update + rs.getLong(1));
                    }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);
    }
}
