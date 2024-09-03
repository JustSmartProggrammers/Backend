package com.example.controller;

import com.example.model.User;
import com.example.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;

@WebServlet("/api/v1/auth/*")
public class AuthController extends HttpServlet {

    private AuthService authService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthService();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        try {
            switch (path) {
                case "/signup":
                    handleSignup(req, resp);
                    break;
                case "/login":
                    handleLogin(req, resp);
                    break;
                case "/logout":
                    handleLogout(req, resp);
                    break;
                case "/check-email":
                    handleCheckEmail(req, resp);
                    break;
                default:
                    sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonRequest = readJsonRequest(req);
        String email = jsonRequest.get("email").getAsString();
        String password = jsonRequest.get("password").getAsString();
        String name = jsonRequest.get("name").getAsString();

        try {
            boolean success = authService.signup(email, password, name);
            if (success) {
                sendJsonResponse(resp, HttpServletResponse.SC_CREATED, createJsonResponse("User created successfully"));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to create user");
            }
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonRequest = readJsonRequest(req);
        String email = jsonRequest.get("email").getAsString();
        String password = jsonRequest.get("password").getAsString();

        User user = authService.login(email, password);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse("Login successful"));
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse("Logout successful"));
    }

    private void handleCheckEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonRequest = readJsonRequest(req);
        String email = jsonRequest.get("email").getAsString();

        if (email == null || email.isEmpty()) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Email is required");
            return;
        }

        boolean isAvailable = authService.isEmailAvailable(email);
        if (isAvailable) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("message", "Email is available");
            sendJsonResponse(resp, HttpServletResponse.SC_OK, jsonResponse);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, "Email is already in use");
        }
    }

    private JsonObject readJsonRequest(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return gson.fromJson(sb.toString(), JsonObject.class);
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, JsonObject jsonResponse) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResponse.toString());
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        sendJsonResponse(resp, statusCode, createJsonResponse(message));
    }

    private JsonObject createJsonResponse(String message) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("message", message);
        return jsonResponse;
    }
}