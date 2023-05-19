package com.hst.dims.databases;

import com.hst.dims.tools.Statics;

import java.sql.*;

public class DatabaseHandler {

    public static final int INVALID_QUERY_ERROR = -3;
    public static final int LOGIN_FAIL_ERROR = -2;
    public static final int DRIVER_INIT_ERROR = -1;
    public static final int COMPLETE = 0;

    private Connection connection;

    public int connect() {
        try {
            Class.forName(Statics.DASEBASE_DRIVER);
            try {
                connection = DriverManager.getConnection(Statics.JDBC_CONNECTION_URL, Statics.DEFAULT_DATABASE_HOST_ID, Statics.DEFAULT_DATABASE_HOST_PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                return LOGIN_FAIL_ERROR;
            }
        } catch (ClassNotFoundException e) {
            return DRIVER_INIT_ERROR;
        }

        return COMPLETE;
    }


    public Connection getConnection() {
        return connection;
    }

    public ResultSet executeQuery(String query) {
        System.out.println("===================================");
        System.out.println("실행된 쿼리 >>> " + query);
        System.out.println("===================================");
        ResultSet rs = null;
        try {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public void excuteUpdate(String query) {
        System.out.println("===================================");
        System.out.println("실행된 쿼리 >>> " + query);
        System.out.println("===================================");
        int rs;
        try {
            Statement stmt = connection.createStatement();
            rs = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
