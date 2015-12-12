package ru.bagrusss.servlets.user;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
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

public class UpdateProfile extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/updateProfile/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `User` SET `about`=?, `name`=? WHERE `email` = ?;
         */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(2);

        sqlParams.add(params.get("about").getAsString());
        sqlParams.add(params.get("name").getAsString());
        String email;
        sqlParams.add(email = params.get("user").getAsString());

        JsonObject result = null;
        try {
            String sql = "UPDATE `User` SET `about`=?, `name`=? WHERE `email` = ?";
            if (mHelper.runPreparedUpdate(mHelper.getConnection(), sql, sqlParams) == 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                Errors.notFound(resp.getWriter());
                return;
            }
            result = getUserDetails(email, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), result);
    }

}
