<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<form action="loginUser" method="post">
    <label>Email: <input type="text" name="email" required/></label><br>
    <label>Password: <input type="password" name="password" required/></label><br>
    <input type="submit" value="Login"/>
    <c:if test="${param.error == 'invalid'}">
        <p>Invalid email or password</p>
    </c:if>
</form>
</body>
</html>
