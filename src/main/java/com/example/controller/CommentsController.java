package com.example.controller;

import com.example.model.Comments;
import com.example.service.CommentsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/v1/posts/*/comments/*")
public class CommentsController extends HttpServlet {

    private CommentsService commentsService;
    private ObjectMapper objectMapper;
    @Setter
    @Getter
    private HttpServletRequest req;

    @Override
    public void init() {
        commentsService = new CommentsService();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        String pathInfo = req.getPathInfo();

        try {
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
                    sendErrorResponse(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다.");
            }
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace(); // 디버깅을 위해 오류 메시지 출력
        }
    }

    private void handleGet(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        this.req = req;
        String[] pathParts = parsePathInfo(pathInfo);

        // 경로 파싱 결과에 따른 조건 검사
        if (pathParts.length == 2 && "comments".equals(pathParts[1])) {
            Long postId = parseLong(pathParts[0]);
            if (postId == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 게시글 ID 형식입니다.");
                return;
            }
            List<Comments> commentsList = commentsService.getCommentsByPostId(postId);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, objectMapper.valueToTree(commentsList));
        } else if (pathParts.length == 3 && "comments".equals(pathParts[1])) {
            Long commentId = parseLong(pathParts[2]);
            if (commentId == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 댓글 ID 형식입니다.");
                return;
            }
            List<Comments> repliesList = commentsService.getRepliesByCommentId(commentId);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, objectMapper.valueToTree(repliesList));
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "잘못된 GET 요청 경로 또는 게시글 ID 형식입니다.");
        }
    }


    private void handlePost(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String[] pathParts = parsePathInfo(pathInfo);

        if (pathParts.length == 2 && "comments".equals(pathParts[1])) {
            Long postId = parseLong(pathParts[0]);
            if (postId == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 게시글 ID 형식입니다.");
                return;
            }

            JsonNode requestBody = readJsonRequest(req);
            if (requestBody == null || !requestBody.has("userId") || !requestBody.has("content")) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "필수 필드 누락: userId 또는 content");
                return;
            }

            Long userId = requestBody.get("userId").asLong();
            String content = requestBody.get("content").asText();

            Comments comment = commentsService.createComment(postId, null, userId, content);
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, objectMapper.valueToTree(comment));
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "잘못된 POST 요청 경로입니다.");
        }
    }

    private void handlePut(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String[] pathParts = parsePathInfo(pathInfo);

        if (pathParts.length == 3 && "comments".equals(pathParts[1])) {
            Long commentId = parseLong(pathParts[2]);
            if (commentId == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 댓글 ID 형식입니다.");
                return;
            }

            JsonNode requestBody = readJsonRequest(req);
            if (requestBody == null || !requestBody.has("content")) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "필수 필드 누락: content");
                return;
            }

            String content = requestBody.get("content").asText();
            boolean isUpdated = commentsService.updateComment(commentId, content);

            if (isUpdated) {
                sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse("댓글이 성공적으로 업데이트되었습니다."));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "댓글을 찾을 수 없습니다.");
            }
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "잘못된 PUT 요청 경로입니다.");
        }
    }

    private void handleDelete(String pathInfo, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        this.req = req;
        String[] pathParts = parsePathInfo(pathInfo);

        if (pathParts.length == 3 && "comments".equals(pathParts[1])) {
            Long commentId = parseLong(pathParts[2]);
            if (commentId == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 댓글 ID 형식입니다.");
                return;
            }
            boolean isDeleted = commentsService.deleteComment(commentId);

            if (isDeleted) {
                sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse("댓글이 성공적으로 삭제되었습니다."));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "댓글을 찾을 수 없습니다.");
            }
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "잘못된 DELETE 요청 경로입니다.");
        }
    }

    // 경로 정보를 파싱하여 의미 있는 배열로 반환
    private String[] parsePathInfo(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().isEmpty()) {
            return new String[0];
        }
        return pathInfo.substring(1).split("/");
    }


    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private JsonNode readJsonRequest(HttpServletRequest req) throws IOException {
        return objectMapper.readTree(req.getReader());
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, JsonNode jsonResponse) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResponse.toString());
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        ObjectNode errorResponse = objectMapper.createObjectNode();
        errorResponse.put("error", message);
        sendJsonResponse(resp, statusCode, errorResponse);
    }

    private JsonNode createJsonResponse(String message) {
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        jsonResponse.put("message", message);
        return jsonResponse;
    }

}
