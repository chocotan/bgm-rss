<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="io.loli.bgm.share.*" %>
<%@ taglib uri="/struts-tags" prefix="struts" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>添加</title>

  </head>
  
  <body>
  <struts:form action="add">
  	<struts:textfield name="email" label="请输入email地址"></struts:textfield>
  	<struts:textfield name="id" label="bangumi帐号id"></struts:textfield>
  	<struts:textfield name="prefix" label="微博前缀, 即显示在最前面的话题如#天羽的番组计划#(不含#)"></struts:textfield>
  	<struts:hidden name="code" value="%{#parameters.code}"></struts:hidden>
  	<struts:submit></struts:submit>
  </struts:form>
  </body>
</html>
