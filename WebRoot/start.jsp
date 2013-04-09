<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="io.loli.bgm.share.UserServiceFactory"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'start.jsp' starting page</title>
  </head>
  
  <body>
  <% 
  	UserServiceFactory.start();
  %>
  </body>
</html>
