package ru.bagrusss.servlets.root;

import com.google.gson.JsonObject;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.helpers.Helper;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class Clear extends BaseServlet {

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
        mSQLBuilder.replace(mSQLBuilder.length() - 2, mSQLBuilder.length(), "");
        try {
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_USER)
                    .append("(`id` INT(11) NOT NULL AUTO_INCREMENT, ")
                    .append("`username` VARCHAR(50) DEFAULT NULL, ")
                    .append("`name` VARCHAR(50), ")
                    .append("`about` BLOB, ")
                    .append("`email` VARCHAR(50) NOT NULL, ")
                    .append("`isAnonymous` TINYINT(1) DEFAULT false,")
                    .append("PRIMARY KEY(`email`), ")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id` ASC)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_FORUM)
                    .append("(`id` INT(5) NOT NULL AUTO_INCREMENT,")
                    .append("`name` VARCHAR(100),")
                    .append("`short_name` VARCHAR(100) NOT NULL,")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`short_name`),")
                    .append("UNIQUE INDEX `name_UNIQUE` (`name`),")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_THREAD)
                    .append("(`id` INT(10) NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isClosed` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`message` BLOB NOT NULL,")
                    .append("`title` VARCHAR(100) NULL,")
                    .append("`slug` VARCHAR(100) NULL,")
                    .append("`likes` INT UNSIGNED NULL DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED NULL DEFAULT 0,")
                    .append("`forum_id` INT NOT NULL,")
                    .append("`forum` VARCHAR(100), ") //forum short_name
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_POST)
                    .append("(`id` INT(12) NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isApproved` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`isEdited` TINYINT(1) DEFAULT false,")
                    .append("`isSpam` TINYINT(1) DEFAULT false,")
                    .append("`message` BLOB,")
                    .append("`likes` INT UNSIGNED DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED DEFAULT 0,")
                    .append("`thread_id` INT NOT NULL,")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("`forum_short_name` VARCHAR(100),")
                    .append("`parent` INT DEFAULT NULL,")
                    .append("PRIMARY KEY (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_FOLLOWERS)
                    .append("(`follower_email` VARCHAR(50) NOT NULL,")
                    .append("`following_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`follower_email`, `following_email`))")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(Helper.TABLE_SUBSCRIPTIONS)
                    .append("(`user_email` VARCHAR(50) NOT NULL,")
                    .append("`thread_id` INT NOT NULL,")
                    .append(" PRIMARY KEY (`user_email`, `thread_id`))")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(mHelper.getConnection(), mSQLBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JsonObject rsp = new JsonObject();
        resp.setStatus(HttpServletResponse.SC_OK);
        rsp.addProperty("code", Errors.CODE_OK);
        rsp.addProperty("response", Errors.MESSAGE_OK);
        resp.getWriter().println(rsp.toString());
    }
}
