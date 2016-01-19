package ru.bagrusss.servlets.post;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav
 */

public class Create extends BaseServlet {
    public static final String URL = BaseServlet.BASE_URL + "/post/create/";


    @SuppressWarnings({"ConstantConditions", "OverlyComplexMethod"})
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(DEFAULT_ENCODING);
        JsonObject params = mGSON.fromJson(req.getReader(), JsonObject.class);
        /*
            INSERT IGNORE INTO `Post` (`thread_id`,`message`, `user_email`, `forum_short_name`, `date`
                `parent`,`isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
         */

        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO")
                .append(DBHelper.TABLE_POST)
                .append("(`thread_id`,`message`, `user_email`, `forum_short_name`, `date`, ");
        List<Object> sqlParams = new ArrayList<>(11);
        long thread;
        sqlParams.add(thread = params.get(THREAD).getAsLong());
        sqlParams.add(params.get(MESSAGE).getAsString());
        sqlParams.add(params.get(USER).getAsString());
        sqlParams.add(params.get(FORUM).getAsString());
        sqlParams.add(params.get(DATE).getAsString());
        byte count = 5;
        long parent = 0;
        if (params.has(PARENT)) {
            try {
                sqlParams.add(parent = params.get(PARENT).getAsLong());
            } catch (UnsupportedOperationException e) {
                sqlParams.add(null);
            }
            sql.append("`parent`, ");
            ++count;
        }
        if (params.has(IS_APPROVED)) {
            sqlParams.add(params.get(IS_APPROVED).getAsBoolean());
            sql.append("`isApproved`, ");
            ++count;
        }
        if (params.has(IS_HIGHLIGHTED)) {
            sqlParams.add(params.get(IS_HIGHLIGHTED).getAsBoolean());
            sql.append("`isHighlighted`, ");
            ++count;
        }
        if (params.has(IS_EDITED)) {
            sqlParams.add(params.get(IS_EDITED).getAsBoolean());
            sql.append("`isEdited`, ");
            ++count;
        }
        if (params.has(IS_SPAM)) {
            sqlParams.add(params.get(IS_SPAM).getAsBoolean());
            sql.append("`isSpam`, ");
            ++count;
        }
        boolean isDeleted = false;
        if (params.has(IS_DELETED)) {
            sqlParams.add(isDeleted = params.get(IS_DELETED).getAsBoolean());
            sql.append("`isDeleted`, ");
            ++count;
        }
        try (Connection connection = mHelper.getConnection()) {
            StringBuilder path = new StringBuilder("SELECT MAX(math_path) FROM ")
                    .append(DBHelper.TABLE_POST).append("WHERE thread_id=")
                    .append(thread).append(" AND parent=");
            byte situation = 0;
            if (parent == 0)
                path.append("NULL");
            else {
                path.append(parent);
                situation |= 1;
            }
            String mathPAth = mHelper.runTypedQuery(connection, path.toString(), rs -> {
                if (rs.next())
                    return rs.getString(1);
                return null;
            });
            path.setLength(0);
            situation <<= 1;
            if (mathPAth != null)
                situation |= 1;
            String parentPath;
            switch (situation) {
                //нет родителя, нет постов - первый пост, начальный id в m_path
                case 0b00:
                    path.append(Integer.toHexString(HEX_PREFIX));
                    break;
                //нет родителя, есть посты - взять максимальный, увеличить id
                case 0b01:
                    path.append(Integer.toHexString(HEX_PREFIX | (Integer.valueOf(mathPAth) + 1)));
                    break;
                //есть родитель, нет постов - взять id родителя, создать id
                case 0b10:
                    path.append("SELECT math_path FROM").append(DBHelper.TABLE_POST)
                            .append(" WHERE id=").append(parent);
                    parentPath = mHelper.runTypedQuery(connection, path.toString(),
                            rs -> {
                                rs.next();
                                return rs.getString(1);
                            });
                    path.setLength(0);
                    path.append(parentPath).append('.').append(Integer.toHexString(HEX_PREFIX));
                    break;
                //есть родитель, есть посты - взять m_path, увеличить на 1 конец
                default:
                    String maxPAth = mathPAth.substring(mathPAth.length() - 5, mathPAth.length() - 1);
                    path.append(mathPAth.substring(0, mathPAth.length() - 6))
                            .append(Integer.valueOf(maxPAth) + 1);
                    break;
            }
            count++;
            sql.append(" math_path").append(") VALUES (");
            for (byte i = 0; i < count - 1; ++i) {
                sql.append("?, ");
            }
            sqlParams.add(path.toString());
            sql.append("? )");
            mHelper.preparedInsertAndGetKeys(connection, sql.toString(), sqlParams, gk -> {
                ResultSetMetaData metaData = gk.getMetaData();
                int columns = metaData.getColumnCount();
                if (gk.next()) {
                    params.addProperty(ID, gk.getLong(1));
                    for (byte i = 2; i <= columns; ++i) {
                        Object obj = gk.getObject(i);
                        params.add(metaData.getColumnName(i), mGSON.toJsonTree(obj == null ? null
                                : gk.getObject(i)));
                    }
                }
            });
            if (!isDeleted) {
                mHelper.runUpdate(connection, "Update Thread SET posts=posts+1 WHERE id=" + thread);
            }
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), params);
    }
}
