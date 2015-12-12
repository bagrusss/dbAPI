package ru.bagrusss.servlets.user;

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
 * Created by vladislav on 20.10.15.
 */

public class Follow extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/follow/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);

        /*
           INSERT IGNORE INTO(`follower_email`, `following_email`) VALUES (? ,?);

           те же запросы, что в user/details/

           SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)
         */

        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(2);
        String user;
        sqlParams.add(user = params.get("followee").getAsString());
        sqlParams.add(params.get("follower").getAsString());
        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO ").append(Helper.TABLE_FOLLOWERS)
                .append("VALUES (?, ?)");
        JsonObject result = null;
        try {
            mHelper.runPreparedUpdate(mHelper.getConnection(), sql.toString(), sqlParams);
            result = getUserDetails(user, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);


    }
}
