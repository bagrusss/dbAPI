package ru.bagrusss.servlets.thread;

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
 * Created by vladislav
 */
public class Details extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/details/";

    final List<String> mRelated = new ArrayList<>();

    public Details() {
        super();
        mRelated.add("user");
        mRelated.add("forum");
    }

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
        long id = Long.valueOf(req.getParameter("thread"));

        if (!checkParam(req.getParameterValues("related"))) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Errors.incorrecRequest(resp.getWriter());
            return;
        }
        JsonObject reslult = null;
        try {
            reslult = getThreadDetails(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Errors.correct(resp.getWriter(), reslult);
    }
}
