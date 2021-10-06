<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Download-Zip</title>
</head>
<body>
Hello <b><%= request.getParameter("id") %></b>!
<br/>
<a href="DownloadFileServlet">Download Zip</a>
</body>
</html>