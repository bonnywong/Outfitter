import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by swebo_000 on 2016-04-14.
 */
public class IndexServlet extends HttpServlet {

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String action = request.getParameter("action");
        String message = "";

        if (action != null) {
            if (action.equals("button1")) {
                message = "Server: Pressed button 1!";
            }
            if (action.equals("button2")) {
                message = "Server: Pressed button 2!";
            }
        }

        try {
            //request.setAttribute("message", message);
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp?message=" + message);
            rd.forward(request, response);
        } catch (ServletException e) {
            System.out.println("Servlet Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }

    }

}
