package ru.bagrusss.servlets.user;

import com.google.gson.JsonArray;
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

public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/details/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        String email = req.getParameter(USER);

        /* SELECT * FROM `User` WHERE `email` = ?;

           SELECT `following_email` FROM `Followers` WHERE `follower_email` = ?;

           SELECT `follower_email` FROM `Followers` WHERE `following_email` =?;

           SELECT `thread_id` FROM `Subscriptions` WHERE `user_email` = ?; индекс (user_email, id)

           */

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(Helper.TABLE_USER)
                .append(" WHERE ").append("`email` = ?;");
        List<Object> sqlParams = new ArrayList<>(1);
        sqlParams.add(email);
        JsonObject result = new JsonObject();
        final boolean[] success = {false};
        try {
            mHelper.runPreparedQuery(mHelper.getConnection(), sqlBuilder.toString(), sqlParams, rs -> {
                if (rs.next()) {
                    result.addProperty(ABOUT, rs.getString(ABOUT));
                    result.addProperty(NAME, rs.getString(NAME));
                    result.addProperty(ID, rs.getInt(ID));
                    result.addProperty(EMAIL, rs.getString(EMAIL));
                    result.addProperty(IS_ANNONIMOUS, rs.getBoolean(IS_ANNONIMOUS));
                    result.addProperty(USERNAME, rs.getString(USERNAME));
                    success[0] = true;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!success[0]) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Errors.notFound(resp.getWriter());
            return;
        }
        JsonArray followers = null;
        JsonArray following = null;
        JsonArray subscriptions = null;
        try {
            followers = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWING_EMAIL, FOLLOWER_EMAIL, sqlParams);
            following = getListByEmail(Helper.TABLE_FOLLOWERS, FOLLOWER_EMAIL, FOLLOWING_EMAIL, sqlParams);
            subscriptions = getSubscriptions(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        result.add(FOLLOWERS, followers);
        result.add(FOLLOWING, following);
        result.add(SUBSCTIPTIOS, subscriptions);

        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);

    }


}
