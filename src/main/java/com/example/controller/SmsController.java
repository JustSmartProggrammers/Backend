package com.example.controller;

import com.example.service.PhoneVerificationService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;

@WebServlet("/sms/*")
public class SmsController extends HttpServlet {

    private PhoneVerificationService phoneVerificationService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        phoneVerificationService = new PhoneVerificationService();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        try {
            switch (path) {
                case "/send-verification":
                    handleSendVerification(req, resp);
                    break;
                case "/verify-code":
                    handleVerifyCode(req, resp);
                    break;
                default:
                    sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private void handleSendVerification(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonRequest = readJsonRequest(req);
        String phoneNumber = jsonRequest.get("phoneNumber").getAsString();

        try {
            String result = phoneVerificationService.sendVerificationCode(phoneNumber);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse(result));
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send verification code: " + e.getMessage());
        }
    }

    private void handleVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonRequest = readJsonRequest(req);
        String phoneNumber = jsonRequest.get("phoneNumber").getAsString();
        String code = jsonRequest.get("code").getAsString();

        boolean isVerified = phoneVerificationService.verifyCode(phoneNumber, code);
        if (isVerified) {
            sendJsonResponse(resp, HttpServletResponse.SC_OK, createJsonResponse("Phone number verified successfully"));
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid verification code");
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