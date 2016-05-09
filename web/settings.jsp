<%@ page import="Models.UserEntity" %><%--
  Created by IntelliJ IDEA.
  User: swebo_000
  Date: 2016-04-18
  Time: 22:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UserEntity user = (UserEntity) session.getAttribute("user");
    if (user == null) {
        request.getSession().invalidate();
%> <jsp:forward page="index.jsp" /> <%
    }
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
                <a href="#"><%=user.getUsername()%></a>
                <ul class="menu">
                    <li><a href="matching.jsp">Matching</a></li>
                    <li><a href="#">My liked outfits</a></li>
                    <li><a href="logout">Logout</a></li>
                    <!-- ... -->
                </ul>
            </li>
        </ul>
    </div>
    <div class="fatText">Settings</div>
    <div id="settings">
        <form method="POST" action="/settings">
            <div class="row">
                <div class="large-4 columns large-centered">
                    <label>
                        <h5>Your Username: <%= user.getUsername()%></h5>
                        <br>
                    </label>
                </div>
            </div>
            <div class="row">
                <div class="large-4 columns large-centered">
                    <label>
                        <h5>Your E-mail: <%= user.getEmail()%></h5>
                        <br>
                    </label>
                </div>
            </div>
            <div class="row">
                <div class="large-4 columns large-centered">
                    <label>
                        <h5>Change Password</h5>
                        <input type="password" placeholder="New password" />
                    </label>
                </div>
            </div>
            <div class="row">
                <div class="large-4 columns large-centered">
                    <label>
                        <h5>Retype New Password</h5>
                        <input type="password" placeholder="New password" />
                    </label>
                </div>
            </div>
            <br>
            <button class="alert tiny button" type="submit" name="action" value="wipe">Debug: wipe weights</button>
            <br>
            <button class="success hollow large button" type="submit" name="action" value="change">Submit</button>
        </form>
    </div>
</div>



<script src="js/vendor/jquery.js"></script>
<script src="js/vendor/what-input.js"></script>
<script src="js/vendor/foundation.js"></script>
<script>
    //Hide the registration form at load.
    $(document).foundation();
</script>
</body>
</html>
