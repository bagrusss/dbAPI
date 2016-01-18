package ru.bagrusss.servlets.root;

import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Errors;
import ru.bagrusss.servlets.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Clear extends BaseServlet {

    public static final String URL = BASE_URL + "/clear/";
    private final StringBuilder mSQLBuilder = new StringBuilder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf8");
        mSQLBuilder.setLength(0);
        mSQLBuilder.append("DROP TABLE IF EXISTS");
        for (String tbl : DBHelper.TABLES) {
            mSQLBuilder.append(tbl)
                    .append(',');
        }
        mSQLBuilder.replace(mSQLBuilder.length() - 2, mSQLBuilder.length(), "");
        try (Connection connection = mHelper.getConnection()) {
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_USER)
                    .append("(`id` INT NOT NULL AUTO_INCREMENT, ")
                    .append("`username` VARCHAR(50) DEFAULT NULL, ")
                    .append("`name` VARCHAR(30), ")
                    .append("`about` BLOB, ")
                    .append("`email` VARCHAR(50) NOT NULL, ")
                    .append("`isAnonymous` TINYINT(1) DEFAULT false,")
                    .append("PRIMARY KEY(`email`), ")
                    .append("UNIQUE INDEX `Name_id` (`name`,`id`), ")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id`) ) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_FORUM)
                    .append("(`id` INT NOT NULL AUTO_INCREMENT,")
                    .append("`name` VARCHAR(100),")
                    .append("`short_name` VARCHAR(100) NOT NULL,")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`short_name`),")
                    .append("UNIQUE INDEX `Name_UNIQUE` (`name`),")
                    .append("UNIQUE INDEX `id_UNIQUE` (`id`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_THREAD)
                    .append("(`id` INT NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isClosed` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`message` BLOB NOT NULL,")
                    .append("`title` VARCHAR(100) NULL,")
                    .append("`slug` VARCHAR(100) NULL,")
                    .append("`posts` INT UNSIGNED DEFAULT 0,")
                    .append("`likes` INT UNSIGNED DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED DEFAULT 0,")
                    .append("`forum_id` INT NOT NULL,")
                    .append("`forum` VARCHAR(100), ")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`id`), ")
                    .append("INDEX `UserEmail_date` (`user_email`, `date`), ")
                    .append("INDEX `Forum_date` (`forum`, `date`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_POST)
                    .append("(`id` INT NOT NULL AUTO_INCREMENT,")
                    .append("`date` TIMESTAMP NULL,")
                    .append("`isApproved` TINYINT(1) DEFAULT false,")
                    .append("`isDeleted` TINYINT(1) DEFAULT false,")
                    .append("`isEdited` TINYINT(1) DEFAULT false,")
                    .append("`isHighlighted` TINYINT(1) DEFAULT false,")
                    .append("`isSpam` TINYINT(1) DEFAULT false,")
                    .append("`message` BLOB,")
                    .append("`likes` INT UNSIGNED DEFAULT 0,")
                    .append("`dislikes` INT UNSIGNED DEFAULT 0,")
                    .append("`thread_id` INT NOT NULL,")
                    .append("`user_email` VARCHAR(50) NOT NULL,")
                    .append("`forum_short_name` VARCHAR(100),")
                    .append("`parent` INT DEFAULT NULL,")
                    .append("PRIMARY KEY (`id`), ")
                    .append("INDEX `UserEmail_Date` (`user_email`, `date`), ") //для user/listPosts
                    .append("INDEX `ThreadId_Date` (`thread_id`, `date`), ") //для post/List
                    .append("INDEX `ForumShortName_Date` (`forum_short_name`, `date`), ") //для post/List
                    .append("INDEX `ForumShortName_UserEmail` (`forum_short_name`, `user_email`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_FOLLOWERS)
                    .append("(`follower_email` VARCHAR(50) NOT NULL,")
                    .append("`following_email` VARCHAR(50) NOT NULL,")
                    .append("PRIMARY KEY (`follower_email`, `following_email`), ")
                    .append("UNIQUE KEY following_follower (`following_email`, `follower_email`)) ")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
            mSQLBuilder.setLength(0);
            mSQLBuilder.append("CREATE TABLE IF NOT EXISTS ").append(DBHelper.TABLE_SUBSCRIPTIONS)
                    .append("(`user_email` VARCHAR(50) NOT NULL,")
                    .append("`thread_id` INT NOT NULL,")
                    .append(" PRIMARY KEY (`user_email`, `thread_id`))")
                    .append("DEFAULT CHARACTER SET = utf8, ENGINE = InnoDB");
            mHelper.runUpdate(connection, mSQLBuilder.toString());
        } catch (SQLException e) {
            Errors.unknownError(resp.getWriter());
            e.printStackTrace();
            return;
        }
        Errors.correct(resp.getWriter(), Errors.MESSAGE_OK);
    }
}
