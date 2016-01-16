package ru.bagrusss.servlets.thread;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by vladislav
 */
public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/details/";

    private boolean checkParam(String[] param) {
        if (param == null)
            return true;
        for (String val : param) {
            if (!val.equals("user") && !val.equals("forum"))
                return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            SELECT *, likes-dislikes AS points FROM `Thread` t WHERE t.`id` = ?
            SELECT * FROM `User` WHERE `email` = ?
            SELECT * FROM `Forum` WHERE `short_name` = ?

         */
        long id = Long.valueOf(req.getParameter(THREAD));
        if (!checkParam(req.getParameterValues(RELATED))) {
            Errors.incorrecRequest(resp.getWriter());
            return;
        }
        JsonObject reslult;
        try(Connection connection = mHelper.getConnection()) {
            reslult = getThreadDetails(connection, id);
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), reslult);
    }
}
