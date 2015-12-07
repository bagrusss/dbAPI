package ru.bagrusss.servlets.user;

import com.google.gson.JsonObject;
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

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/user/create/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            INSERT IGNORE INTO `User` (`username`, `about`, `name`, `email`, `isAnonymous`)
            VALUES (?, ?, ?, ?, ?);

            SELECT * FROM `User` WHERE `email` = ?;
        */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        JsonObject response = new JsonObject();
        List<Object> sqlParams = new ArrayList<>(5);
        String email;
        try {
            sqlParams.add(email = params.get(EMAIL).getAsString());
            sqlParams.add(params.get(USERNAME).getAsString());
            sqlParams.add(params.get(ABOUT).getAsString());
            sqlParams.add(params.get(NAME).getAsString());
            sqlParams.add(params.has(IS_ANNONIMOUS) && params.get(IS_ANNONIMOUS).getAsBoolean());
            /*String email = params.get(EMAIL).getAsString();
            String username = params.get(USERNAME).getAsString();
            String about = params.get(ABOUT).getAsString();
            String name = params.get(NAME).getAsString();
            Boolean isAnnonimous = params.has(IS_ANNONIMOUS) && params.get(IS_ANNONIMOUS).getAsBoolean();*/

        } catch (UnsupportedOperationException e) {
            //e.printStackTrace();
            response.addProperty("code", CODE_INCORRECT_REQUEST);
            response.addProperty("response", MESSAGE_INCORRECT_REQUEST);
            resp.getWriter().write(response.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        try {
            StringBuilder sql = new StringBuilder("INSERT IGNORE INTO `User` (`email`, `username`, `about`, `name`, `isAnnonimous`) ")
                    .append("VALUES (?, ?, ?, ?, ?);");
            if (mHelper.runPreparedUpdate(mHelper.getConnection(), sql.toString(), sqlParams) == 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                response.addProperty("code", CODE_USER_ALREADY_EXISTS);
                response.addProperty("response", MESSAGE_USER_ALREADY_EXISTS);
                resp.getWriter().write(response.toString());
                return;
            }
            sql.setLength(0);
            sqlParams.clear();
            sqlParams.add(email);
            sql.append(" SELECT * FROM `User` WHERE `email` = ?;");
            mHelper.runPreparedQuery(mHelper.getConnection(), sql.toString(), sqlParams, rs -> {
                if (rs.next()) {
                    params.addProperty(ID, rs.getInt(ID));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.addProperty("code", CODE_OK);
        response.add("response", params);
        resp.getWriter().write(response.toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
