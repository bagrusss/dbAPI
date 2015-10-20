package ru.bagrusss.helpers;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vladislav on 20.10.15.
 */

public interface DBIntarface {

    String TABLE_USER = "";
    String TABLE_THREAD = "";
    String TABLE_POST = "";
    String TABLE_FORUM = "";

    ResultSet selectQuery(@NotNull String sql) throws SQLException;

    int updateOrInsertQuery(@NotNull String sql) throws SQLException;

    boolean execute(@NotNull String sql) throws SQLException;

}
