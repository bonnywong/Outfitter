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
        String session = request.getSession().getId();

        if (action != null) {
            if (action.equals("login")) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                System.out.println("Server: Login.");
                System.out.println("Username:" + username + ", Password: " + password);
                System.out.println("SessionID: " + session);
                //authUser()
                //If auth successful.
                try {
                    RequestDispatcher rd = request.getRequestDispatcher("matching.jsp");
                    rd.forward(request, response);
                } catch (ServletException e) {
                    System.out.println("Servlet Exception: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO Exception: " + e.getMessage());
                }

            }
            if (action.equals("register")) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String password2 = request.getParameter("password2");
                String email = request.getParameter("email");

                System.out.println("Server: Register User");
                System.out.println("Username:" + username + ", Password: " + password + ", Retyped password: " + password2 + ", Email: " + email);
                System.out.println("SessionID: " + session);

                try {
                    RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
                    rd.forward(request, response);
                } catch (ServletException e) {
                    System.out.println("Servlet Exception: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO Exception: " + e.getMessage());
                }
            }
        } else {
            //Nothing at the moment?
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    private void dispatch() {

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
