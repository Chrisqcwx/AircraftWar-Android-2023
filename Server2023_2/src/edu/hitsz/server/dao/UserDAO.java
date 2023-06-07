package edu.hitsz.server.dao;

import org.json.JSONObject;

import java.io.Closeable;
import java.sql.*;

public class UserDAO extends BaseDAO {

    public UserDAO(String tableName) {
        super(tableName);
    }

    public static void createUserTable(String tableName) {

        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(dbName);
            stmt = c.createStatement();

            String sql = "CREATE TABLE " + tableName +
                    " (NAME CHAR(50) PRIMARY KEY     NOT NULL," +
                    " PASSWORD   CHAR(50)     NOT NULL," +
                    "SCORE INT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
//            System.exit(0);
        }
    }

    public void insert(String usrname, String password, int score) throws SQLException {
        String sql = "INSERT INTO " + tableName +" (NAME, PASSWORD, SCORE)" +
                " VALUES (?, ?, ?)";
//        try {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, usrname);
        ps.setString(2, password);
        ps.setInt(3, score);
        ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    public void updateScore(String usrname, int score) {
        String sql = "UPDATE " + tableName + " SET SCORE=? WHERE NAME=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, score);
            ps.setString(2, usrname);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public UserInfo selectName(String usrname) {
        String sql = "SELECT * FROM " + tableName + " where NAME=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usrname);
            ResultSet sqlRes = ps.executeQuery();
            if (sqlRes.next()) {
                return new UserInfo(
                        sqlRes.getString("NAME"),
                        sqlRes.getString("PASSWORD"),
                        sqlRes.getInt("SCORE")
                );
            }else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {

//        UserDAO dao = new UserDAO("aaa");
//        UserInfo info = dao.selectName("weishen");
//        System.out.println(info);
//
//        dao.updateScore("weishen", 100);
//
//        info = dao.selectName("weishen");
//        System.out.println(info);
//
//        dao.close();


    }

    @Override
    public void close(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
