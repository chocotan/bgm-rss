<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="struts" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>删除</title>
  </head>
  
  <body>
  <struts:form action="add!remove.action">
  	<struts:textfield name="email" label="请输入想删除的邮箱名"></struts:textfield>
  	<struts:submit></struts:submit>
  </struts:form>
  </body>
</html>
