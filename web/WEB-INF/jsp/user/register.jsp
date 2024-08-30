<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"> <!-- 문자 인코딩을 UTF-8로 설정 -->
    <title>사용자 등록</title>
</head>
<body>
<h1>사용자 등록</h1>
<form action="registerUser" method="post">
    <label for="email">이메일:</label>
    <input type="email" id="email" name="email" required><br><br>
    <label for="password">비밀번호:</label>
    <input type="password" id="password" name="password" required><br><br>
    <label for="name">이름:</label>
    <input type="text" id="name" name="name" required><br><br>
    <input type="submit" value="등록">
</form>
</body>
</html>
