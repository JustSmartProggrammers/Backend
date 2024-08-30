package com.example.dao;

import com.example.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public void insertUser(String email, String password, String name, boolean isDeleted) {
        String sql = "INSERT INTO user (email, password, name, isDeleted, createdAt) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setBoolean(4, isDeleted);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("사용자가 성공적으로 삽입되었습니다.");
            } else {
                System.out.println("사용자 삽입에 실패하였습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
