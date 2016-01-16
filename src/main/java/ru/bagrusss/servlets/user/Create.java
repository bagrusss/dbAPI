package ru.bagrusss.servlets.user;

import com.google.gson.JsonElement;
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
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        List<Object> sqlParams = new ArrayList<>(5);
        sqlParams.add(params.get(EMAIL).getAsString());
        JsonElement elem = params.get(USERNAME);
        sqlParams.add(elem.isJsonNull() ? null : elem.getAsString());
        elem = params.get(ABOUT);
        sqlParams.add(elem.isJsonNull() ? null : elem.getAsString());
        elem = params.get(NAME);
        sqlParams.add(elem.isJsonNull() ? null : elem.getAsString());
        sqlParams.add(params.has(IS_ANNONIMOUS) && params.get(IS_ANNONIMOUS).getAsBoolean());
        long id = 0;
        try (Connection connection = mHelper.getConnection()) {
            String sql = "INSERT IGNORE INTO `User` (`email`, `username`, `about`, `name`, `isAnonymous`) VALUES (?, ?, ?, ?, ?);";
            id = mHelper.preparedInsertAndGetKeys(connection, sql, sqlParams);
            if (id == 0) {
                Errors.userAlreadyExists(resp.getWriter());
                return;
            }
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        params.addProperty(ID, id);
        Errors.correct(resp.getWriter(), params);
    }
}
