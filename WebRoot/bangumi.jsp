<%
	request.setCharacterEncoding("UTF-8");
	response.setContentType("text/xml;charset=UTF-8");
	response.getWriter().write(io.loli.bgm.rome.RssFactory.getRssString(request.getParameter("id")));
	response.flushBuffer();  
	response.setContentType("application/octet-stream");
%>