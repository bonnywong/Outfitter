<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <link rel="stylesheet" type="text/css" href="css/foundation.css" >
        <link href='https://fonts.googleapis.com/css?family=Arvo:700,700italic' rel='stylesheet' type='text/css'>
    </head>
    
    <body>
    <div id="container">
            <div class="fatText">Welcome to Outfitter!</div>
            <div id="login">
                <form method="POST" action="index">
                    <div class="row">
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>Username</h5>
                                <input type="text" name="username" placeholder="Username" />
                            </label> 
                        </div>
                    </div>
                    <div class="row"> 
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>Password</h5>
                                <input type="password" name="password" placeholder="Password" />
                            </label> 
                        </div>
                    </div>
                    Not yet a member? Click <a href id="registrationLink">here</a> to register a new account.
                    <br>
                    <br>
                    <button class="success hollow large button" type="submit" name="action" value="login">Login</button>
                </form>
            </div>
            <div id="register">
                <form>
                    <div class="row">
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>Username</h5>
                                <input type="text" placeholder="Username" />
                            </label> 
                        </div>
                    </div>
                    <div class="row"> 
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>E-mail</h5>
                                <input type="text" placeholder="user@domain.com" />
                            </label> 
                        </div>
                    </div>
                    <div class="row"> 
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>Password</h5>
                                <input type="password" placeholder="Password" />
                            </label> 
                        </div>
                    </div>
                    <div class="row"> 
                        <div class="large-4 columns large-centered">
                            <label>
                                <h5>Retype Password</h5>
                                <input type="password" placeholder="Password" />
                            </label> 
                        </div>
                    </div>
                    <br>
                    <button class="success hollow large button" type="submit" name="action" value="register">Register</button>
                </form>
            </div>
    </div>



    <script src="js/vendor/jquery.js"></script>
    <script src="js/vendor/what-input.js"></script>
    <script src="js/vendor/foundation.js"></script>
    <script>
        //Hide the registration form at load. 
        $("#register").hide(); 
        $(document).foundation();
        
        $(document).ready(function(){
            $("#registrationLink").click(function() {
               $("#login").toggle(350);  
               $("#register").toggle(350);
               return false; //Prevent following of the link.
            });
        });
    </script>
    </body>
</html>
