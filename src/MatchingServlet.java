import Models.UserEntity;

import javax.jws.soap.SOAPBinding;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by swebo_000 on 2016-04-16.
 */
public class MatchingServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String action = request.getParameter("action");
        String sessionId = request.getSession().getId();
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");
        if (action != null) {
            System.out.println("Updating weights for: " + user.getUsername() + ". ID: " + user.getUserId());
            if (action.equals("like")) {
                System.out.println("Server: Pressed \"Like\" button");
                //TODO: Update weights for this specific user and fetch new clothing.
            }
            if (action.equals("meh")) {
                System.out.println("Server: Pressed \"Meh\" button");
                //TODO: Update weights for this specific user and fetch new clothing.
            }
            if (action.equals("dislike")) {
                System.out.println("Server: Pressed \"Dislike\" button");
                //TODO: Update weights for this specific user and fetch new clothing.
            }
            System.out.println("SessionId: " + sessionId);
        }

        try {
            RequestDispatcher rd = request.getRequestDispatcher("matching.jsp");
            rd.forward(request, response);
        } catch (ServletException e) {
            System.out.println("Servlet Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
