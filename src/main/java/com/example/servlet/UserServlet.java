package com.example.servlet;

import com.example.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registerUser")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        boolean isDeleted = false; // 기본값 설정

        userDAO.insertUser(email, password, name, isDeleted);

        // 성공 페이지로 리다이렉트
        response.sendRedirect("WEB-INF/jsp/user/success.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 사용자 등록 폼을 보여주는 페이지
        request.getRequestDispatcher("/WEB-INF/jsp/user/register.jsp").forward(request, response);
    }
}
