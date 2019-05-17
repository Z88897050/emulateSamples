package com.jdbc.bean;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public class WrapConnect implements Closeable {

    private Connection connection;
    private String db;
    private String user;

    public WrapConnect(Connection connection, String db, String user) {
        this.connection = connection;
        this.db = db;
        this.user = user;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDb() {
        return db;
    }

    public String getUser() {
        return user;
    }

    @Override
    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException ignored) {
        }
    }
}