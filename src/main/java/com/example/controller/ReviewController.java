package com.example.controller;

import com.example.model.Review;
import com.example.service.ReviewService;
import com.example.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/v1/review/*")
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        reviewService = new ReviewService();
        objectMapper = JacksonConfig.getObjectMapper();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String method = req.getMethod();
            String pathInfo = req.getPathInfo();

            switch (method) {
                case "GET":
                    handleGet(pathInfo, req, resp);
                    break;
                case "POST":
                    handlePost(pathInfo, req, resp);
                    break;
                case "PUT":
                    handlePut(pathInfo, req, resp);
                    break;
                case "DELETE":
                    handleDelete(pathInfo, req, resp);
                    break;
                default:
                    sendErrorResponse(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed");
            }
        } catch (Exception e) {
            log("Unexpected error occurred: ", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void handleGet(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        switch (pathParts[1]) {
            case "user":
                if (pathParts.length == 3) {
                    Long userId = Long.parseLong(pathParts[2]);
                    handleGetReviewsByUserId(userId, resp);
                } else {
                    sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
                }
                break;
            case "spot":
                if (pathParts.length == 3) {
                    Long spotId = Long.parseLong(pathParts[2]);
                    handleGetReviewsBySpotId(spotId, resp);
                } else {
                    sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid spot ID");
                }
                break;
            default:
                if (pathParts.length == 2) {
                    Long reviewId = Long.parseLong(pathParts[1]);
                    handleGetReviewById(reviewId, resp);
                } else {
                    sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid review ID");
                }
        }
    }

    private void handlePost(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                JsonNode jsonRequest = readJsonRequest(req);
                Review review = objectMapper.treeToValue(jsonRequest, Review.class);
                Review createdReview = reviewService.createReview(review);
                sendJsonResponse(resp, HttpServletResponse.SC_CREATED, createdReview);
            } catch (JsonProcessingException e) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
            } catch (SQLException e) {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            }
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path for POST request");
        }
    }

    private void handlePut(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Review ID is required");
            return;
        }

        try {
            Long reviewId = Long.parseLong(pathInfo.substring(1));
            JsonNode jsonRequest = readJsonRequest(req);

            if (!jsonRequest.has("content")) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Content is required");
                return;
            }

            String content = jsonRequest.get("content").asText();

            Review updatedReview = reviewService.updateReviewContent(reviewId, content);
            if (updatedReview != null) {
                sendJsonResponse(resp, HttpServletResponse.SC_OK, updatedReview);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Review not found");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid review ID");
        } catch (SQLException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private void handleDelete(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Review ID is required");
            return;
        }

        try {
            Long reviewId = Long.parseLong(pathInfo.substring(1));
            reviewService.deleteReview(reviewId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully");
            sendJsonResponse(resp, HttpServletResponse.SC_OK, response);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid review ID");
        } catch (SQLException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private void handleGetReviewsByUserId(Long userId, HttpServletResponse resp) throws IOException, SQLException {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        sendJsonResponse(resp, HttpServletResponse.SC_OK, reviews);
    }

    private void handleGetReviewsBySpotId(Long spotId, HttpServletResponse resp) throws IOException, SQLException {
        List<Review> reviews = reviewService.getReviewsBySpotId(spotId);
        sendJsonResponse(resp, HttpServletResponse.SC_OK, reviews);
    }

    private void handleGetReviewById(Long reviewId, HttpServletResponse resp) throws IOException, SQLException {
        Review review = reviewService.getReviewById(reviewId);
        if (review != null) {
            sendJsonResponse(resp, HttpServletResponse.SC_OK, review);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Review not found");
        }
    }

    private JsonNode readJsonRequest(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return objectMapper.readTree(sb.toString());
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        sendJsonResponse(resp, statusCode, errorResponse);
    }
}