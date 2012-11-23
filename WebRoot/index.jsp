<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>天羽的bgm同步到微博小工具</title>
	
  </head>
  
  <body>
  1.这是一个把bangumi的时间胶囊同步到新浪微博的小工具<br/>
  2.本工具还不完善，可能会出现各种猎奇现象，请谨慎使用<br/>
  3.不出意外应该不会重复发已经发过的微博<br/>
  4.如有疑问请去http://loli.io留言<br/>
  <br/>
  点击下面的链接<br/>
  <a href="https://api.weibo.com/oauth2/authorize?client_id=3616446582&redirect_uri=http://bgm-rss.loli.io/code.jsp">授权</a>
  </body>
</html>