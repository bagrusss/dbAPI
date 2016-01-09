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
 * Created by vladislav
 */
public class Remove extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/remove/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        //индекс по id, thread_id ?
        long id = params.get("post").getAsLong();
        /*
              UPDATE `Post` SET `isDeleted`=1 WHERE `id`=?;
         */
        try {
            Connection connection = mHelper.getConnection();
            toggleField(Helper.TABLE_POST, id, "isDeleted", true);
            mHelper.runQuery(connection, "SELECT `thread_id` FROM "
                    + Helper.TABLE_POST + " Where id=" + id, rs -> {
                if (rs.next())
                    try (Statement st = connection.createStatement()) {
                        st.executeUpdate("UPDATE " + Helper.TABLE_THREAD
                                + "SET `posts`=`posts`-1 WHERE `id`=" + rs.getLong(1));
                    }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), params);
    }
}
