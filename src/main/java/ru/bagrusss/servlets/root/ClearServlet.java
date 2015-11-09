package ru.bagrusss.servlets.root;

import org.json.JSONException;
import org.json.JSONObject;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vladislav on 19.10.15.
 */

public class ClearServlet extends BaseServlet {

    public static final String URL = BASE_URL + "/clear/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSONObject rsp = new JSONObject();
       /* StringBuilder sql = new StringBuilder("show tables;");
        Statement statement = null;
        try {
            statement = mHelper.connectToDB();
            statement.execute(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                mHelper.closeConnection();*/
        try {
            rsp.put("code", CODE_OK);
            rsp.put("response", MESSAGE_OK);
        } catch (JSONException e) {
            e.printStackTrace();

        }

           /* } catch (SQLException | JSONException e) {
                e.printStackTrace();
            }
        }*/
        resp.getWriter().println(rsp.toString());
    }
}
