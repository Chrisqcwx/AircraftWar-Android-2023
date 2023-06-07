package edu.hitsz.server.dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class HistoryRecordDAO extends BaseDAO {


    public HistoryRecordDAO(String tableName) {
        super(tableName);
    }
    public static void createHistoryTable(String tableName) {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(dbName);
            stmt = c.createStatement();
            String sql = "CREATE TABLE " + tableName +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME CHAR(50)     NOT NULL," +
                    "SCORE INT NOT NULL," +
                    "TIME CHAR(20) NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
//            System.exit(0);
        }
    }



    public void insert(String usrname, int score, String time) {
        String sql = "INSERT INTO " + tableName +" (ID, NAME, SCORE, TIME)" +
                " VALUES (NULL, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usrname);
            ps.setInt(2, score);
            ps.setString(3, time);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insert(HistoryRecord rec) {
        insert(rec.name, rec.score, rec.time);
    }

    public List<HistoryRecord> getHead100() {
        String sql = "SELECT * FROM "+tableName+" ORDER BY SCORE DESC LIMIT 100";
        try {
            Statement stmt = conn.createStatement();
            List<HistoryRecord> res = new LinkedList<>();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int score = rs.getInt("SCORE");
                String time = rs.getString("TIME");
                String name = rs.getString("NAME");
                res.add(new HistoryRecord(name, score, time));
            }

            stmt.close();
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new LinkedList<>();
        }
    }


    public static void main(String[] args) {
//        createHistoryTable("history");
//        createUserTable("aaa");
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
