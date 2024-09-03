package com.example.service;

import com.example.dao.ReviewDAO;
import com.example.model.Review;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewService {
    private ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }
    public Review createReview(Review review) throws SQLException {
        review.setCreatedAt(LocalDateTime.now());
        review.setDeleted(false);
        return reviewDAO.createReview(review);
    }

    public List<Review> getReviewsByUserId(Long userId) throws SQLException {
        return reviewDAO.getReviewsByUserId(userId);
    }

    public List<Review> getReviewsBySpotId(Long spotId) throws SQLException {
        return reviewDAO.getReviewsBySpotId(spotId);
    }

    public Review getReviewById(Long reviewId) throws SQLException {
        return reviewDAO.getReviewById(reviewId);
    }

    public void updateReview(Review review) throws SQLException {
        reviewDAO.updateReview(review);
    }

    public void deleteReview(Long reviewId) throws SQLException {
        reviewDAO.deleteReview(reviewId);
    }
}