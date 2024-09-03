<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.model.Spot" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title><%= ((Spot)request.getAttribute("spot")).getName() %> - Spot Details</title>
  <link rel="stylesheet" type="text/css" href="/css/styles.css">
</head>
<body>
<div class="container">
  <h1><%= ((Spot)request.getAttribute("spot")).getName() %></h1>
  <div class="spot-details">
    <p><strong>Address:</strong> <%= ((Spot)request.getAttribute("spot")).getAddress() %></p>
    <p><strong>Contact:</strong> <%= ((Spot)request.getAttribute("spot")).getContact() %></p>
    <p><strong>Region Type:</strong> <%= ((Spot)request.getAttribute("spot")).getRegionType() %></p>
    <p><strong>Sports Type:</strong> <%= ((Spot)request.getAttribute("spot")).getSportsType() %></p>
    <p><strong>Operating Hours:</strong></p>
    <ul>
      <li>Weekdays: <%= ((Spot)request.getAttribute("spot")).getWeekdayHours() %></li>
      <li>Weekends: <%= ((Spot)request.getAttribute("spot")).getWeekendHours() %></li>
    </ul>
    <p><strong>Fee:</strong> <%= ((Spot)request.getAttribute("spot")).getFee() %></p>
    <p><strong>Parking:</strong> <%= ((Spot)request.getAttribute("spot")).getParking() %></p>
    <p><strong>Rental Available:</strong> <%= ((Spot)request.getAttribute("spot")).getRentalAvailable() %></p>
    <%
      String reservation = ((Spot)request.getAttribute("spot")).getReservation();
      if (reservation != null && !reservation.isEmpty()) {
    %>
    <p><strong>Reservation:</strong> <a href="<%= reservation %>" target="_blank">Make a Reservation</a></p>
    <% } %>
  </div>
  <a href="/spots" class="back-link">Back to Spot List</a>
</div>
</body>
</html>