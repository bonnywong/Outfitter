import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        String message = "";

        if (action != null) {
            if (action.equals("login")) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                System.out.println("Server: Login.");
                System.out.println("Username:" + username + ", Password: " + password);
                message = "Server: Pressed login button!";

                //authUser()
                //If auth successful.
                try {
                    //request.setAttribute("message", message);
                    RequestDispatcher rd = request.getRequestDispatcher("matching.jsp");
                    rd.forward(request, response);
                } catch (ServletException e) {
                    System.out.println("Servlet Exception: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO Exception: " + e.getMessage());
                }

            }
            if (action.equals("register")) {
                System.out.println("Server: Register User");
                message = "Server: Pressed register button!";

                //
            }
        } else {
            //Nothing at the moment?
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        
    }

    /**
     * Create a new user.
     */
    private void newUser() {
        //TODO: Register user.
    }

    /**
     * Log in an existing user
     */
    private void authUser() {
        //TODO: Authenticate user.
    }

}
