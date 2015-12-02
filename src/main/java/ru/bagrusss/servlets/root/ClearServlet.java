package ru.bagrusss.servlets.root;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ru.bagrusss.helpers.DBHelper;
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
        mSQLBuilder.setLength(0);
        resp.setContentType("application/json");
        mSQLBuilder.append("DROP TABLE IF EXISTS");
        for (String tbl : Helper.TABLES) {
            mSQLBuilder.append(tbl).append(',');
        }
        mSQLBuilder.replace(mSQLBuilder.length() - 2, mSQLBuilder.length(), ";");
        try {
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_USER)
                    .append("(`id` INT(11) NOT NULL AUTO_INCREMENT, ")
                    .append("`username` VARCHAR(50) DEFAULT NULL, ")
                    .append("`name` VARCHAR(50), ")
                    .append("`about` VARCHAR(120), ")
                    .append("`email` VARCHAR(50) NOT NULL, ")
                    .append("`isAnnonimous` TINYINT(1) DEFAULT false,")
                    .append("PRIMARY KEY(`email`), ")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id` ASC)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB; \n");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_FORUM)
                    .append("(`id` INT(5) NOT NULL AUTO_INCREMENT,")
                    .append("`name` VARCHAR(100),")
                    .append("`short_name` VARCHAR(100) NOT NULL,")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`short_name`),")
                    .append("UNIQUE INDEX `name_UNIQUE` (`name` ASC),")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id` ASC)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB;");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_THREAD)
                    .append("(`id` INT(10) NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isClosed` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`message` VARCHAR(255) NOT NULL,")
                    .append("`title` VARCHAR(100) NULL,")
                    .append("`slug` VARCHAR(100) NULL,")
                    .append("`likes` INT UNSIGNED NULL DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED NULL DEFAULT 0,")
                    .append("`Forum_id` INT NOT NULL,")
                    .append("`User_id` INT NOT NULL,")
                    .append("PRIMARY KEY (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB;");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_POST)
                    .append("(`id` INT(12) NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isApproved` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`isEdited` TINYINT(1) DEFAULT false,")
                    .append("`isSpam` TINYINT(1) DEFAULT false,")
                    .append("`message` VARCHAR(255) ,")
                    .append("`likes` INT UNSIGNED DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED DEFAULT 0,")
                    .append("`thread` INT NOT NULL,")
                    .append("`user` INT NOT NULL,")
                    .append("`parent` INT ,")
                    .append("PRIMARY KEY (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB;");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_FOLLOWERS)
                    .append("(`follower_email` VARCHAR(50) NOT NULL,")
                    .append("`followee_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`follower_email`, `followee_email`))")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB;");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_SUBSCRIPTIONS)
                    .append("(`user_email` VARCHAR(50) NOT NULL,")
                    .append("`Thread_id` INT NOT NULL,")
                    .append(" PRIMARY KEY (`User_email`, `Thread_id`))")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB;");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JsonObject rsp = new JsonObject();
        resp.setStatus(HttpServletResponse.SC_OK);
        rsp.addProperty("code", CODE_OK);
        rsp.addProperty("response", MESSAGE_OK);
        resp.getWriter().println(rsp.toString());
    }
}
