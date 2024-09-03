package com.example.dao;

import com.example.util.DBUtil;
import com.example.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public Review createReview(Review review) throws SQLException {
        String sql = "INSERT INTO review (userId, spotId, content, createdAt, isDeleted) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getSpotId());
            stmt.setString(3, review.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(review.getCreatedAt()));
            stmt.setBoolean(5, review.isDeleted());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }
        }
        return review;
    }

    public List<Review> getReviewsByUserId(Long userId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE userId = ? AND isDeleted = false";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    public List<Review> getReviewsBySpotId(Long spotId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE spotId = ? AND isDeleted = false";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, spotId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    public Review getReviewById(Long reviewId) throws SQLException {
        String sql = "SELECT * FROM review WHERE id = ? AND isDeleted = false";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        return null;
    }

    public void updateReview(Review review) throws SQLException {
        String sql = "UPDATE review SET content = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getContent());
            stmt.setLong(2, review.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteReview(Long reviewId) throws SQLException {
        String sql = "UPDATE review SET isDeleted = true WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reviewId);
            stmt.executeUpdate();
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setUserId(rs.getLong("userId"));
        review.setSpotId(rs.getLong("spotId"));
        review.setContent(rs.getString("content"));
        review.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        review.setDeleted(rs.getBoolean("isDeleted"));
        return review;
    }
}