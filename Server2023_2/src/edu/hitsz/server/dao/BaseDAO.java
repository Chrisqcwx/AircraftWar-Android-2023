package edu.hitsz.server.dao;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO implements Closeable {
    protected static final String dbName = "jdbc:sqlite:data/test.db";
    protected Connection conn;

    protected String tableName;

    protected BaseDAO(String tableName) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(dbName);
            this.tableName = tableName;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
