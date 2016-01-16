package ru.bagrusss.servlets.user;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav
 */
public class Unfollow extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/unfollow/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        /*
           DELETE FROM `Followers` WHERE follower_email = ? AND following_email = ?;

           SELECT * FROM `User` WHERE `email` = ?;
           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;
           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;
           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?;
        */
        List<Object> sqlParams = new ArrayList<>(2);
        String email;
        sqlParams.add(email = params.get(FOLLOWEE).getAsString());
        sqlParams.add(params.get(FOLLOWER).getAsString());
        JsonObject res;
        try (Connection connection = mHelper.getConnection()) {
            StringBuilder sql = new StringBuilder("DELETE FROM")
                    .append(Helper.TABLE_FOLLOWERS)
                    .append("WHERE follower_email = ? AND following_email = ?");
            if (mHelper.runPreparedUpdate(connection, sql.toString(), sqlParams) == 0) {
                Errors.notFound(resp.getWriter());
                return;
            }
            res = getUserDetails(connection, email, true);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), res);
    }

}
