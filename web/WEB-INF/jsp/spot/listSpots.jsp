<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.model.Spot" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>스포츠 시설 목록</title>
</head>
<body>
<h1>스포츠 시설 목록</h1>
<table border="1">
  <thead>
  <tr>
    <th>ID</th>
    <th>이름</th>
    <th>주소</th>
    <th>연락처</th>
    <th>예약</th>
    <th>지역</th>
    <th>종목</th>
    <th>주중 운영 시간</th>
    <th>주말 운영 시간</th>
    <th>요금</th>
    <th>주차</th>
    <th>대여 가능 여부</th>
  </tr>
  </thead>
  <tbody>
  <%
    List<Spot> spots = (List<Spot>) request.getAttribute("spots");
    if (spots != null) {
      for (Spot spot : spots) {
  %>
  <tr>
    <td><%= spot.getId() %></td>
    <td><%= spot.getName() %></td>
    <td><%= spot.getAddress() %></td>
    <td><%= spot.getContact() %></td>
    <td><%= spot.getReservation() %></td>
    <td><%= spot.getRegionType() %></td>
    <td><%= spot.getSportsType() %></td>
    <td><%= spot.getWeekdayHours() %></td>
    <td><%= spot.getWeekendHours() %></td>
    <td><%= spot.getFee() %></td>
    <td><%= spot.getParking() %></td>
    <td><%= spot.getRentalAvailable() %></td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="12">데이터가 없습니다.</td>
  </tr>
  <%
    }
  %>
  </tbody>
</table>
</body>
</html>
