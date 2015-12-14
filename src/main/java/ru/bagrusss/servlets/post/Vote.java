package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by vladislav on 19.10.15.
 */
public class Vote extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/vote/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        /*
            UPDATE `Post` SET `likes`=`likes`+ 1 WHERE `id` = ?
            UPDATE `Post` SET `dislikes`=`dislikes`+ 1 WHERE `id` = ?
         */
        JsonObject params = mGson.fromJson(req.getReader(), JsonObject.class);
        long id = params.get("post").getAsLong();
        byte vote = params.get("vote").getAsByte();
        try {
            params = this.vote(Helper.TABLE_POST, id, vote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        if (params != null){
            Errors.correct(resp.getWriter(), params);
        }
    }
}
