package ru.bagrusss.helpers;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vladislav on 20.10.15.
 */

public interface DBIntarface {

    String TABLE_USER = "";
    String TABLE_THREAD = "";
    String TABLE_POST = "";
    String TABLE_FORUM = "";
    boolean testDB();
    Statement connectToDB() throws SQLException;
    void closeConnection() throws SQLException;


}
