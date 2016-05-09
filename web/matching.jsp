<%@ page import="Models.UserEntity" %><%--
  Created by IntelliJ IDEA.
  User: swebo_000
  Date: 2016-04-18
  Time: 18:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UserEntity user = (UserEntity) session.getAttribute("user");
    if (user == null) {
        request.getSession().invalidate();
        %> <jsp:forward page="index.jsp" /> <%
    }

    //Imagine we've received products here.
    int topId = (Integer) request.getSession().getAttribute("topId"); //leading 0 removed because else Java thinks it's an octal
    int botId = (Integer) request.getSession().getAttribute("botId"); //

    //Send these to the servlet
    //TODO: Do the reverse, call Servlet and forward to JSP.......
    //request.setAttribute("idTop", idTop);
    //request.setAttribute("idBot", idBot);
%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <link rel="stylesheet" href="css/foundation.css" >
    <link href='https://fonts.googleapis.com/css?family=Arvo:700,700italic' rel='stylesheet' type='text/css'>
</head>

<body>
<div id="container">
    <div class="menucontainer">
        <ul class="dropdown menu" data-dropdown-menu>
            <li class=".is-dropdown-submenu-parent">
                <a href="#"><%= user.getUsername() %></a>
                <ul class="menu">
                    <li><a href="settings.jsp">Settings</a></li>
                    <li><a href="#">My liked outfits</a></li>
                    <li><a href="logout">Logout</a></li>
                    <!-- ... -->
                </ul>
            </li>
        </ul>
    </div>
    <div id="fatText"></div>
    <div id="matching">
        <div class="imagecontainer">
            <div class="image">
                <img src="image/0<%= topId %>"/>
            </div>
            <div class="description">
                Description here.
            </div>
        </div>
        <br>
        <div class="imagecontainer">
            <div class="image">
                <img src="image/0<%= botId %>"/>
            </div>
            <div class="description">
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
                Description here.
            </div>
        </div>
        <br>
        <form action="matching" method="GET">
            <button class="success hollow large button" type="submit" name="action" value="like" style="margin: 10px">Like!</button>
            <button class="warning hollow large button" type="submit" name="action" value="meh" style="margin: 10px">Meh</button>
            <button class="alert hollow large button" type="submit" name="action" value="dislike" style="margin: 10px">Dislike!</button>
        </form>
    </div>
</div>


<script src="js/vendor/jquery.js"></script>
<script src="js/vendor/what-input.js"></script>
<script src="js/vendor/foundation.js"></script>
<script>
    $(document).foundation();
</script>
</body>
</html>
