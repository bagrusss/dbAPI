package ru.bagrusss.servlets.root;


import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by vladislav
 */

public class Status extends BaseServlet {

    public static final String URL = BaseServlet.BASE_URL + "/status/";

    StringBuilder sql = new StringBuilder("SELECT COUNT(id) ic FROM User UNION ALL ")
            .append("SELECT COUNT(id) tc FROM Thread UNION ALL ")
            .append("SELECT COUNT(id) fc FROM Forum UNION ALL ")
            .append("SELECT COUNT(id) pc FROM Post");
    final String[] mNames = new String[]{"users", "threads", "forums", "posts"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        req.getPathInfo();
        JsonObject states = new JsonObject();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql.toString(), (rs) -> {
                byte i = 0;
                while (rs.next())
                    states.addProperty(mNames[i++], rs.getInt(1));
            });
            Errors.correct(resp.getWriter(), states);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

