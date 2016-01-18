package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.DBHelper;
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
@SuppressWarnings("SqlNoDataSourceInspection")
public class Remove extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/remove/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        long id = params.get(POST).getAsLong();
        /*
              UPDATE `Post` SET `isDeleted`=1 WHERE `id`=?;
         */
        try (Connection connection = mHelper.getConnection()) {
            toggleField(connection, DBHelper.TABLE_POST, id, IS_DELETED, true);
            mHelper.runQuery(connection, "SELECT `thread_id` FROM "
                    + DBHelper.TABLE_POST + " Where id=" + id, rs -> {
                if (rs.next())
                    try (Statement st = connection.createStatement()) {
                        st.executeUpdate("UPDATE " + DBHelper.TABLE_THREAD
                                + "SET `posts`=`posts`-1 WHERE `id`=" + rs.getLong(1));
                    }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
