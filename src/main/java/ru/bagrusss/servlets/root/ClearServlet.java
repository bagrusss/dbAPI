package ru.bagrusss.servlets.root;

import org.json.JSONException;
import org.json.JSONObject;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public class ClearServlet extends BaseServlet {

    public static final String URL = BASE_URL + "/clear/";
    private StringBuilder mSQLBuilder = new StringBuilder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        mSQLBuilder.append("DROP TABLE IF EXISTS");
        for (String tbl: Helper.TABLES) {
            mSQLBuilder.append(tbl).append(',');
        }
        mSQLBuilder.replace(mSQLBuilder.length() - 2, mSQLBuilder.length(), ";");
        try {
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        JSONObject rsp = new JSONObject();
        try {
            rsp.put("code", CODE_OK);
            rsp.put("response", MESSAGE_OK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resp.getWriter().println(rsp.toString());
    }
}
