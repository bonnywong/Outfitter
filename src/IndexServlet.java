import Authentication.UserLogic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles the login and registering step of the service.
 *
 * Created by swebo_000 on 2016-04-14.
 */
public class IndexServlet extends HttpServlet {

    public void init() {

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        String action = request.getParameter("action");
        String sessionId = request.getSession().getId();

        if (action != null) {

            UserLogic userLogic = new UserLogic(); //Remember to close this.

            if (action.equals("login")) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                System.out.println("Server: Login.");
                System.out.println("Username:" + username + ", Password: " + password);
                System.out.println("SessionID: " + sessionId);

                //User authentication below:
                if (userLogic.authUser(username, password)) {
                    System.out.println("User exists and successfully authenticated!");
                    //Attach UserEntity to the session.
                    request.getSession().setAttribute("user", userLogic.getUser(username));
                    dispatch(request, response, "/matching", "");
                } else {
                    System.out.println("Wrong username or password inserted!");
                    dispatch(request, response, "index.jsp", "");
                    //TODO: Display some kind of message?

                }

                userLogic.close();
            }

            if (action.equals("register")) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String password2 = request.getParameter("password2");
                String email = request.getParameter("email");

                System.out.println("Server: Register User");
                System.out.println("Username:" + username + ", Password: " + password + ", Retyped password: " + password2 + ", Email: " + email);
                System.out.println("SessionID: " + sessionId);

                //User registration below.
                if(userLogic.registerUser(username, password, email)) {
                    System.out.println("Registration successful!");

                } else {
                    System.out.println("Registration failed!");
                }
                dispatch(request, response, "index.jsp", "");
                userLogic.close();
            }
        } else {
            //Nothing at the moment?
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response, String location, String message) {
        try {
            RequestDispatcher rd = request.getRequestDispatcher(location);
            rd.forward(request, response);
        } catch (ServletException e) {
            System.out.println("Servlet Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
