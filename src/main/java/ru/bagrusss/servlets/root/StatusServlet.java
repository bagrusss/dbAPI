package ru.bagrusss.servlets.root;


import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav on 19.10.15.
 */

public class StatusServlet extends BaseServlet {

    public static final String URL = BaseServlet.BASE_URL + "/status/";

    final String sql = (new StringBuilder(" SELECT COUNT(User.id), COUNT(Thread.id), COUNT(Post.id), COUNT(Forum.id)"))
            .append("FROM User, Thread, Forum, Post ").toString();
    final String[] mNames = new String[]{"users", "threads", "forums", "posts"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        req.getPathInfo();
        JsonObject res = new JsonObject();
        try {
            mHelper.runQuery(mHelper.getConnection(), sql, (rs) -> {
                JsonObject states = new JsonObject();
                if (rs.next())
                    for (byte i = 0; i < 4; ++i) {
                        states.addProperty(mNames[i], rs.getInt(1 + i));
                    }
                res.addProperty("code", CODE_OK);
                res.add("response", states);
            });
            resp.getWriter().println(res.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

