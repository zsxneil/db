package com.my.shardingjdbc.base;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

    public static void queryTest(DataSource dataSource) {
        String sql = "SELECT t.* FROM table_sharding t WHERE t.id in (?, ?, ?)";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 6);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 11);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while(rs.next()) {
                    System.out.println(rs.getInt(1));
                    System.out.println(rs.getString(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void singleQueryTest(DataSource dataSource) {
        String sql = "SELECT t.* FROM table_sharding t WHERE t.id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 5);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while(rs.next()) {
                    System.out.println(rs.getInt(1));
                    System.out.println(rs.getString(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTest(DataSource dataSource) {
        String sql = "update table_sharding set uuid = '123456' WHERE id in (?, ?, ?)";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 5);
            preparedStatement.setInt(2, 2);
            preparedStatement.setInt(3, 11);
            int count = preparedStatement.executeUpdate();
            System.out.println("update success count: " + count);
//            try (ResultSet rs = preparedStatement.executeQuery()) {
//                while(rs.next()) {
//                    System.out.println(rs.getInt(1));
//                    System.out.println(rs.getString(2));
//                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
