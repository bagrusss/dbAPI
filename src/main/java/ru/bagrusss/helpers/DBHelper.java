package ru.bagrusss.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vladislav on 20.10.15.
 */

public class DBHelper implements DBIntarface {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";
    private Connection mConnection;

    private static DBHelper s_instance;

    public static DBHelper getInstance() {
        DBHelper localInstance = s_instance;
        if (localInstance == null) {
            synchronized (DBHelper.class) {
                localInstance = s_instance;
                if (localInstance == null) s_instance = localInstance = new DBHelper();
            }
        }
        return localInstance;
    }

    @Override
    public boolean testDB() {
        DBIntarface hlp = DBHelper.getInstance();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement statement = hlp.connectToDB();
            if (statement != null)
                statement.close();
            hlp.closeConnection();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Firstly you must close returned statement!<br>
     * Seconly you must call closeConnection() method!
     *
     * @return Statement
     * @throws SQLException
     */
    @Override
    public Statement connectToDB() throws SQLException {
        mConnection = DriverManager.getConnection(DB_HOST, DB_USER, DB_PASS);
        return mConnection.createStatement();
    }

    @Override
    public void closeConnection() throws SQLException {
        if (mConnection != null)
            mConnection.close();
    }

}
