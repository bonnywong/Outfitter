<%--
  Created by IntelliJ IDEA.
  User: swebo_000
  Date: 2016-04-14
  Time: 00:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message = request.getParameter("message");
%>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <link rel="stylesheet" href="css/foundation.css" >
        <link href='https://fonts.googleapis.com/css?family=Arvo:700,700italic' rel='stylesheet' type='text/css'>
    </head>

    <body>
    <div class="vertalign-parent" id="demoWrapper">
            <div class="vertandcenter">
                <div id="fatText">Hello, World!!</div>
                <h4> <%=request.getParameter("message")%></h4>
                <form action="index" method="GET">
                    <button class="success hollow large button" type="submit" name="action" value="button1">Button 1</button>
                    <button class="alert hollow large button" type="submit" name="action" value="button2">Button 2</button>
                </form>
            </div>
        </div>




    <%--
     All required JS files goes here.
    --%>
    <script src="js/vendor/jquery.js"></script>
    <script src="js/vendor/what-input.js"></script>
    <script src="js/foundation.js"></script>
    <script>
        $(document).foundation();
    </script>
    </body>
</html>
