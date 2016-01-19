package ru.bagrusss.servlets.thread;

import com.google.gson.JsonArray;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by vladislav on 19.10.15.
 */
public class ListPosts extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/thread/listPosts/";


    @Override
    @SuppressWarnings("Duplicates")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        int thread = Integer.valueOf(req.getParameter(THREAD));
        /*
            SELECT *, likes-dislikes AS points FROM `Post` WHERE `thread_id` = ?
         */
        StringBuilder sql = new StringBuilder("SELECT id, isApproved, isDeleted, isEdited, isHighlighted, isSpam, ")
                .append("message, likes, dislikes, thread_id, user_email, forum_short_name, parent, ")
                .append("DATE_FORMAT(date, '%Y-%m-%d %H:%i:%s') pd,  likes-CAST(dislikes AS SIGNED) points ")
                .append("FROM").append(DBHelper.TABLE_POST).append("WHERE `thread_id`=").append(thread);
        String par = req.getParameter(SINCE);
        if (par != null)
            sql.append(" AND `date` >= \'").append(par).append("\' ");
        String sort = req.getParameter(SORT);
        String order = req.getParameter(ORDER);
        if (order == null)
            order = "desc";
        if (sort == null)
            sort = FLAT;
        par = req.getParameter(LIMIT);
        switch (sort) {
            case FLAT:
                sql.append(" ORDER BY `date` ").append(order);
                if (par != null)
                    sql.append(" LIMIT ").append(par);
                break;
            case TREE:
                sql.append(" ORDER BY `math_path` ").append(order);
                if (par != null)
                    sql.append(" LIMIT ").append(par);
                break;
            case PARENT_TREE:
                sql.append(" ORDER BY `math_path` ").append(order);
                if (par != null) {
                    int limit = Integer.valueOf(par);
                    StringBuilder limitCountSQL = new StringBuilder("SELECT count(*) FROM")
                            .append(DBHelper.TABLE_POST).append("WHERE thread_id=").append(thread)
                            .append(" AND math_path");
                    if (!order.equals("desc")) {
                        limitCountSQL.append("<=");
                    } else limitCountSQL.append(">=");
                    limitCountSQL.append(limit);
                    try {
                        try (Connection connection = mHelper.getConnection()) {
                            limit = mHelper.runTypedQuery(connection, limitCountSQL.toString(), rs -> {
                                rs.next();
                                return rs.getInt(1);
                            });
                        }
                        sql.append(" LIMIT ").append(limit);
                    } catch (SQLException e) {
                        Errors.unknownError(resp.getWriter());
                        e.printStackTrace();
                        return;
                    }
                }
                break;
            default:
                Errors.incorrecRequest(resp.getWriter());
                return;
        }
        JsonArray postsList = new JsonArray();
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runQuery(connection, sql.toString(), rs -> {
                while (rs.next()) {
                    postsList.add(parsePost(rs, null));
                }
            });
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), postsList);
    }
}
