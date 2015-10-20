package ru.bagrusss.helpers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

/**
 * Created by vladislav on 20.10.15.
 */
public class DBHelper implements DBIntarface {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/tp_db";
    private static final String DB_USER = "tp_user";
    private static final String DB_PASS = "tp_user2015";
    private Connection mConnection;
    private Statement mStatement;

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

    private Statement connectToDB() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mConnection = DriverManager.getConnection(DB_HOST, DB_USER, DB_PASS);
        return mConnection.createStatement();
    }

    @Override
    @Nullable
    public ResultSet selectQuery(@NotNull String sql) throws SQLException {
        ResultSet rs = null;
        try {
            mStatement = connectToDB();
            rs = mStatement.executeQuery(sql);
        } finally {
            if (mStatement != null)
                mStatement.close();
            if (mConnection != null)
                mConnection.close();
        }
        return rs;
    }

    @Override
    public int updateOrInsertQuery(@NotNull String sql) throws SQLException {
        int res = 0;
        try {
            mStatement = connectToDB();
            res = mStatement.executeUpdate(sql);
        } finally {
            if (mStatement != null)
                mStatement.close();
            if (mConnection != null)
                mConnection.close();
        }
        return res;
    }

    @Override
    public boolean execute(@NotNull String sql) throws SQLException{
        boolean res = false;
        try {
            mStatement = connectToDB();
            res = mStatement.execute(sql);
        } finally {
            if (mStatement != null)
                mStatement.close();
            if (mConnection != null)
                mConnection.close();
        }
        return res;
    }

}
